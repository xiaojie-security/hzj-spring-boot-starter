package com.hzj.amap.core.webapi.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 兼容高德接口中同一字段返回字符串、数组或空值的 Gson 适配器。
 */
public final class FlexibleStringTypeAdapter extends TypeAdapter<String> {

    @Override
    public String read(JsonReader reader) throws IOException {
        return readValue(reader);
    }

    @Override
    public void write(JsonWriter writer, String value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        writer.value(value);
    }

    private String readValue(JsonReader reader) throws IOException {
        JsonToken token = reader.peek();
        return switch (token) {
            case NULL -> {
                reader.nextNull();
                yield null;
            }
            case STRING, NUMBER, BOOLEAN -> reader.nextString();
            case BEGIN_ARRAY -> readArray(reader);
            default -> {
                reader.skipValue();
                yield null;
            }
        };
    }

    private String readArray(JsonReader reader) throws IOException {
        List<String> values = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            String value = readValue(reader);
            if (value != null && !value.isBlank()) {
                values.add(value.trim());
            }
        }
        reader.endArray();
        return values.isEmpty() ? null : String.join(",", values);
    }
}
