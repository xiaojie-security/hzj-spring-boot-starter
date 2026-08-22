package com.hzj.amap.core.webapi.adapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

/**
 * 创建高德接口专用 ObjectMapper。
 */
public final class AMapObjectMapperFactory {

    private AMapObjectMapperFactory() {
    }

    public static ObjectMapper create() {
        return configure(new ObjectMapper());
    }

    public static ObjectMapper configure(ObjectMapper objectMapper) {
        ObjectMapper mapper = objectMapper.copy();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.registerModule(new AMapJacksonModule());
        return mapper;
    }
}
