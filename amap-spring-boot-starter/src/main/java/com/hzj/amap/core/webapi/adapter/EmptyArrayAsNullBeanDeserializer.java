package com.hzj.amap.core.webapi.adapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * 兼容高德接口将缺失对象字段返回为空数组的情况。
 */
public final class EmptyArrayAsNullBeanDeserializer extends JsonDeserializer<Object>
        implements ResolvableDeserializer, ContextualDeserializer {

    private final JsonDeserializer<Object> delegate;

    public EmptyArrayAsNullBeanDeserializer(JsonDeserializer<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.START_ARRAY) {
            parser.skipChildren();
            return null;
        }
        return delegate.deserialize(parser, context);
    }

    @Override
    public void resolve(DeserializationContext context) throws JsonMappingException {
        if (delegate instanceof ResolvableDeserializer resolvable) {
            resolvable.resolve(context);
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context,
                                                 BeanProperty property) throws JsonMappingException {
        if (delegate instanceof ContextualDeserializer contextual) {
            @SuppressWarnings("unchecked")
            JsonDeserializer<Object> contextualDelegate = (JsonDeserializer<Object>) contextual
                    .createContextual(context, property);
            return new EmptyArrayAsNullBeanDeserializer(contextualDelegate);
        }
        return this;
    }
}
