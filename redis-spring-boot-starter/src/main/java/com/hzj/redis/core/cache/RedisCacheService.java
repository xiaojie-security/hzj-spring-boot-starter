package com.hzj.redis.core.cache;

import com.hzj.redis.core.lock.RedisLockService;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存服务。
 * <p>
 * 提供缓存旁路查询、空值占位和分布式锁双检能力，用于降低缓存穿透和缓存击穿风险。
 * </p>
 */
@Slf4j
public class RedisCacheService implements RedisOperations<String, Object> {

    private static final String EMPTY_PLACEHOLDER = "::NULL_PLACEHOLDER::";
    private static final String BREAKDOWN_LOCK_PREFIX = "redis:cache:breakdown:";

    private final RedisLockService lockService;

    @Delegate
    private final RedisTemplate<String,Object> redisTemplate;

    public RedisCacheService(RedisLockService lockService, RedisTemplate<String,Object> redisTemplate) {
        this.lockService = Objects.requireNonNull(lockService, "RedisLockService 不能为空");
        this.redisTemplate = redisTemplate;
    }

    /**
     * 查询缓存，未命中时加载数据并缓存结果。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 非空数据缓存时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     * @throws InterruptedException 等待分布式锁过程中线程被中断
     */
    public <T> T getOrLoad(String key, CacheLoader<T> loader, long cacheTtl, TimeUnit timeUnit)
            throws InterruptedException {
        return getOrLoadWithBreakdownProtection(key, loader, cacheTtl, cacheTtl, timeUnit);
    }

    /**
     * 使用空值占位防止缓存穿透。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 非空数据缓存时间
     * @param nullCacheTtl 空值占位缓存时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     */
    public <T> T getOrLoadWithPenetrationProtection(String key, CacheLoader<T> loader,
                                                     long cacheTtl, long nullCacheTtl, TimeUnit timeUnit) {
        validateArguments(key, loader);
        validateTtl(cacheTtl, "cacheTtl");
        validateTtl(nullCacheTtl, "nullCacheTtl");
        Objects.requireNonNull(timeUnit, "timeUnit 不能为空");

        CacheReadResult<T> cached = readCache(key);
        if (cached.hit()) {
            return cached.value();
        }
        T value = loader.load();
        writeCache(key, value, value == null ? nullCacheTtl : cacheTtl, timeUnit);
        return value;
    }

    /**
     * 使用默认空值缓存时间防止缓存穿透。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 缓存时间，空值占位使用相同时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     */
    public <T> T getOrLoadWithPenetrationProtection(String key, CacheLoader<T> loader,
                                                     long cacheTtl, TimeUnit timeUnit) {
        return getOrLoadWithPenetrationProtection(key, loader, cacheTtl, cacheTtl, timeUnit);
    }

    /**
     * 使用分布式锁和二次检查防止缓存击穿。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 非空数据缓存时间
     * @param nullCacheTtl 空值占位缓存时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     * @throws InterruptedException 等待分布式锁过程中线程被中断
     */
    public <T> T getOrLoadWithBreakdownProtection(String key, CacheLoader<T> loader,
                                                   long cacheTtl, long nullCacheTtl, TimeUnit timeUnit)
            throws InterruptedException {
        validateArguments(key, loader);
        validateTtl(cacheTtl, "cacheTtl");
        validateTtl(nullCacheTtl, "nullCacheTtl");
        Objects.requireNonNull(timeUnit, "timeUnit 不能为空");

        CacheReadResult<T> cached = readCache(key);
        if (cached.hit()) {
            return cached.value();
        }

        String lockName = BREAKDOWN_LOCK_PREFIX + key;
        boolean locked = false;
        try {
            locked = lockService.tryLock(lockName);
            if (!locked) {
                log.error("RedisCacheService.getOrLoadWithBreakdownProtection 缓存击穿保护获取锁失败, key={}", key);
                throw new IllegalStateException("缓存击穿保护获取锁失败: " + key);
            }

            cached = readCache(key);
            if (cached.hit()) {
                return cached.value();
            }

            T value = loader.load();
            writeCache(key, value, value == null ? nullCacheTtl : cacheTtl, timeUnit);
            return value;
        } finally {
            if (locked) {
                lockService.unlock(lockName);
            }
        }
    }

    /**
     * 使用默认空值缓存时间防止缓存击穿。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 缓存时间，空值占位使用相同时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     * @throws InterruptedException 等待分布式锁过程中线程被中断
     */
    public <T> T getOrLoadWithBreakdownProtection(String key, CacheLoader<T> loader,
                                                   long cacheTtl, TimeUnit timeUnit)
            throws InterruptedException {
        return getOrLoadWithBreakdownProtection(key, loader, cacheTtl, cacheTtl, timeUnit);
    }

    private <T> CacheReadResult<T> readCache(String key) {
        Object cachedValue = opsForValue().get(key);
        if (cachedValue == null) {
            return CacheReadResult.miss();
        }
        if (EMPTY_PLACEHOLDER.equals(cachedValue)) {
            return CacheReadResult.hit(null);
        }
        @SuppressWarnings("unchecked")
        T value = (T) cachedValue;
        return CacheReadResult.hit(value);
    }

    private void writeCache(String key, Object value, long ttl, TimeUnit timeUnit) {
        opsForValue().set(key, value == null ? EMPTY_PLACEHOLDER : value, ttl, timeUnit);
    }

    private void validateArguments(String key, CacheLoader<?> loader) {
        if (!org.springframework.util.StringUtils.hasText(key)) {
            throw new IllegalArgumentException("缓存键不能为空");
        }
        Objects.requireNonNull(loader, "CacheLoader 不能为空");
    }

    private void validateTtl(long ttl, String fieldName) {
        if (ttl <= 0) {
            throw new IllegalArgumentException(fieldName + " 必须大于0");
        }
    }

    private record CacheReadResult<T>(boolean hit, T value) {

        private static <T> CacheReadResult<T> hit(T value) {
            return new CacheReadResult<>(true, value);
        }

        private static <T> CacheReadResult<T> miss() {
            return new CacheReadResult<>(false, null);
        }
    }
}
