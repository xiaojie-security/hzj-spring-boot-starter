package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.entity.RateLimitResult;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.entity.RedisRateLimitConfig;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.List;
import java.util.UUID;

/**
 * Redis滑动窗口限流实现。
 * <p>使用Redis有序集合记录请求时间戳，并由Lua脚本完成清理、统计和新增。</p>
 */
public class SlidingWindowRedisRateLimiter extends AbstractRedisRateLimiter {

    private static final String SCRIPT = """
            local time = redis.call('TIME')
            local now = time[1] * 1000 + math.floor(time[2] / 1000)
            local window = tonumber(ARGV[1])
            local start = now - window
            redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', start)
            local current = redis.call('ZCARD', KEYS[1])
            local limit = tonumber(ARGV[2])
            local permits = tonumber(ARGV[3])
            local allowed = 0
            if current + permits <= limit then
                for index = 1, permits do
                    redis.call('ZADD', KEYS[1], now, ARGV[4] .. ':' .. index)
                end
                current = current + permits
                allowed = 1
                redis.call('PEXPIRE', KEYS[1], window)
            end
            local remaining = limit - current
            if remaining < 0 then
                remaining = 0
            end
            local retryAfter = 0
            if allowed == 0 then
                local oldest = redis.call('ZRANGE', KEYS[1], 0, 0, 'WITHSCORES')
                if #oldest > 0 then
                    retryAfter = tonumber(oldest[2]) + window - now
                end
            end
            return {allowed, remaining, retryAfter}
            """;

    /**
     * 创建滑动窗口限流器。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     */
    public SlidingWindowRedisRateLimiter(RedissonClient client,
                                         RedisRateLimitConfigProvider configProvider) {
        super(client, configProvider);
    }

    /**
     * 执行滑动窗口限流脚本。
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
                windowMillis,
                config.getLimit(),
                permits,
                UUID.randomUUID().toString());
        return toResult(key, config, values);
    }

    /**
     * 返回滑动窗口算法key片段。
     *
     * @return sliding-window
     */
    @Override
    protected String algorithmKey() {
        return "sliding-window";
    }
}
