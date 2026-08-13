package com.hzj.redis.core.cache;

@FunctionalInterface
public interface CacheLoader<T> {

    T load();
}
