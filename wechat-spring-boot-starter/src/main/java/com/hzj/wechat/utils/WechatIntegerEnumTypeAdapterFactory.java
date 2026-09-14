package com.hzj.wechat.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 取值类型为整型的微信枚举适配器工厂。
 *
 * <p>用于让 {@link WechatIntegerEnum} 的实现类在 JSON 中以数字而不是字符串的形式读写。
 * 使用方式：在枚举类上标注
 * {@code @JsonAdapter(WechatIntegerEnumTypeAdapterFactory.class)}。</p>
 */
public class WechatIntegerEnumTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (!rawType.isEnum() || !WechatIntegerEnum.class.isAssignableFrom(rawType)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Class<? extends WechatIntegerEnum> enumType = (Class<? extends WechatIntegerEnum>) rawType;
        Map<Integer, WechatIntegerEnum> constantsByValue = new HashMap<>();
        for (WechatIntegerEnum constant : enumType.getEnumConstants()) {
            constantsByValue.put(constant.getValue(), constant);
        }
        TypeAdapter<WechatIntegerEnum> adapter = new TypeAdapter<WechatIntegerEnum>() {
            @Override
            public void write(JsonWriter out, WechatIntegerEnum value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.value(value.getValue());
            }

            @Override
            public WechatIntegerEnum read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                return constantsByValue.get(in.nextInt());
            }
        };
        @SuppressWarnings("unchecked")
        TypeAdapter<T> typedAdapter = (TypeAdapter<T>) adapter;
        return typedAdapter;
    }
}
