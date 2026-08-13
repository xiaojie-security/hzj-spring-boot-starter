package com.hzj.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
public final class JsonUtils {
    // 日期格式化常量
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Long转字符串，防止前端精度丢失
            SimpleModule longModule = new SimpleModule();
            longModule.addSerializer(Long.class, ToStringSerializer.instance);
            longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            mapper.registerModule(longModule);

            // Java8 全时间类型序列化/反序列化
            JavaTimeModule timeModule = new JavaTimeModule();
            DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern(DATE_PATTERN);
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern(TIME_PATTERN);

            // 序列化
            timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFmt));
            timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFmt));
            timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFmt));
            // 反序列化
            timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFmt));
            timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFmt));
            timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFmt));
            mapper.registerModule(timeModule);

            // 通用容错配置
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            // 输出空值（可选，根据业务关闭）
            // mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            OBJECT_MAPPER = mapper;
        } catch (Exception e) {
            log.error("ObjectMapper 初始化失败", e);
            throw new RuntimeException("JSON工具初始化异常", e);
        }
    }

    private JsonUtils() {
        throw new AssertionError("禁止实例化工具类");
    }

    // ====================== 序列化 ======================
    public static String writeAsString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("JSON序列化失败, obj={}", obj, e);
            throw new RuntimeException("JSON序列化异常", e);
        }
    }

    // ====================== 反序列化单个对象 ======================
    public static <T> T readValue(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            log.warn("JSON反序列化实体失败, json={}, clazz={}", jsonStr, clazz.getName(), e);
            throw new RuntimeException("JSON反序列化异常", e);
        }
    }

    // ====================== 反序列化 List 集合 ======================
    public static <T> List<T> readList(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, clazz));
        } catch (JsonProcessingException e) {
            log.warn("JSON反序列化List失败, json={}, clazz={}", jsonStr, clazz.getName(), e);
            throw new RuntimeException("JSON List解析异常", e);
        }
    }

    // ====================== 反序列化 Map ======================
    public static <K, V> Map<K, V> readMap(String jsonStr, Class<K> keyCls, Class<V> valCls) {
        if (jsonStr == null || jsonStr.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyCls, valCls));
        } catch (JsonProcessingException e) {
            log.warn("JSON反序列化Map失败, json={}", jsonStr, e);
            throw new RuntimeException("JSON Map解析异常", e);
        }
    }

    // 复杂泛型兜底（TypeReference，例如 List<Map<String,User>>）
    public static <T> T readType(String jsonStr, TypeReference<T> typeRef) {
        if (jsonStr == null || jsonStr.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, typeRef);
        } catch (JsonProcessingException e) {
            log.warn("JSON复杂泛型解析失败, json={}", jsonStr, e);
            throw new RuntimeException("JSON泛型解析异常", e);
        }
    }

    // 对外暴露Mapper，特殊场景自定义扩展
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}