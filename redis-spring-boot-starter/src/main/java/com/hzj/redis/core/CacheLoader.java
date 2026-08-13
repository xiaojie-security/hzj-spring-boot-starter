package com.hzj.redis.core;

@FunctionalInterface
public interface CacheLoader<T> {

    T load();
}
