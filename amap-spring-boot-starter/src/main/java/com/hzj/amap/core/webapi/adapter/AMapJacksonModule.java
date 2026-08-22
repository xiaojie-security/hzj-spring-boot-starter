package com.hzj.amap.core.webapi.adapter;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * 高德 Web API 专用 Jackson 兼容模块。
 */
public final class AMapJacksonModule extends SimpleModule {

    private static final String DOMAIN_PACKAGE = "com.hzj.amap.core.webapi.domain";

    public AMapJacksonModule() {
        super("AMapJacksonModule");
        addDeserializer(String.class, new FlexibleStringDeserializer());
        setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                            BeanDescription beanDescription,
                                                            JsonDeserializer<?> deserializer) {
                Class<?> beanClass = beanDescription.getBeanClass();
                if (!beanClass.getName().startsWith(DOMAIN_PACKAGE)
                        || beanClass.equals(String.class)) {
                    return deserializer;
                }
                @SuppressWarnings("unchecked")
                JsonDeserializer<Object> delegate = (JsonDeserializer<Object>) deserializer;
                return new EmptyArrayAsNullBeanDeserializer(delegate);
            }
        });
    }
}
