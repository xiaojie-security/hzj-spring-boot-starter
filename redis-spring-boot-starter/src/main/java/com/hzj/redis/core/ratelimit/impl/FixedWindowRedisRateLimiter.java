package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.entity.RateLimitResult;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.entity.RedisRateLimitConfig;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.List;

/**
 * Redis固定窗口限流实现。
 * <p>计数、首次过期时间设置和是否放行在同一段Lua脚本中完成。</p>
 */
public class FixedWindowRedisRateLimiter extends AbstractRedisRateLimiter {

    private static final String SCRIPT = """
            local existed = redis.call('EXISTS', KEYS[1])
            local current = redis.call('INCRBY', KEYS[1], ARGV[1])
            local ttl = redis.call('PTTL', KEYS[1])
            if existed == 0 or ttl < 0 then
                redis.call('PEXPIRE', KEYS[1], ARGV[2])
                ttl = tonumber(ARGV[2])
            end
            local limit = tonumber(ARGV[3])
            local remaining = limit - current
            if remaining < 0 then
                remaining = 0
            end
            local allowed = 0
            if current <= limit then
                allowed = 1
            end
            return {allowed, remaining, ttl}
            """;

    /**
     * 创建固定窗口限流器。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     */
    public FixedWindowRedisRateLimiter(RedissonClient client,
                                       RedisRateLimitConfigProvider configProvider) {
        super(client, configProvider);
    }

    /**
     * 执行固定窗口限流脚本。
     *
     * @param key 完整Redis限流key
     * @param config 限流配置
     * @param permits 请求配额数量
     * @param windowMillis 窗口毫秒数
     * @return 限流结果
     */
    @Override
    protected RateLimitResult doAcquire(String key, RedisRateLimitConfig config,
                                        long permits, long windowMillis) {
        List<?> values = client.getScript(StringCodec.INSTANCE).eval(
                RScript.Mode.READ_WRITE,
                SCRIPT,
                RScript.ReturnType.MULTI,
                List.of(key),
                permits,
                windowMillis,
                config.getLimit());
        return toResult(key, config, values);
    }

    /**
     * 返回固定窗口算法key片段。
     *
     * @return fixed-window
     */
    @Override
    protected String algorithmKey() {
        return "fixed-window";
    }
}
