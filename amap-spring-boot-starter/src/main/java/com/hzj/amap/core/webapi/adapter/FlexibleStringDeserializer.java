package com.hzj.amap.core.webapi.adapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 将高德接口中字符串、数字、布尔值、数组和空值统一解析为字符串。
 */
public final class FlexibleStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return toStringValue(node);
    }

    private String toStringValue(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isTextual() || node.isNumber() || node.isBoolean()) {
            return node.asText();
        }
        if (!node.isArray()) {
            return null;
        }

        List<String> values = new ArrayList<>();
        for (JsonNode child : node) {
            String value = toStringValue(child);
            if (value != null && !value.isBlank()) {
                values.add(value.trim());
            }
        }
        return values.isEmpty() ? null : String.join(",", values);
    }
}
