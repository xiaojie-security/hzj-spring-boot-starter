package com.hzj.amap.core.webapi.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * 兼容高德接口将缺失对象字段返回为空数组的 Gson 适配器工厂。
 */
public final class EmptyArrayAsNullTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (!isObjectType(rawType)) {
            return null;
        }

        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<>() {
            @Override
            public T read(JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.skipValue();
                    }
                    reader.endArray();
                    return null;
                }
                return delegate.read(reader);
            }

            @Override
            public void write(JsonWriter writer, T value) throws IOException {
                delegate.write(writer, value);
            }
        };
    }

    private boolean isObjectType(Class<?> rawType) {
        return !rawType.isPrimitive()
                && !rawType.isEnum()
                && !rawType.isArray()
                && !Number.class.isAssignableFrom(rawType)
                && !Boolean.class.equals(rawType)
                && !Character.class.equals(rawType)
                && !String.class.equals(rawType)
                && !Collection.class.isAssignableFrom(rawType)
                && !Map.class.isAssignableFrom(rawType)
                && !com.google.gson.JsonElement.class.isAssignableFrom(rawType)
                && !Object.class.equals(rawType);
    }
}
