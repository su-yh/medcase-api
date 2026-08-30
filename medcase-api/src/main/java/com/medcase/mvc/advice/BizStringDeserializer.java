package com.medcase.mvc.advice;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.jdk.StringDeserializer;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author suyh
 * @since 2024-09-13
 */
@ControllerAdvice
public class BizStringDeserializer extends StringDeserializer {
    public final static BizStringDeserializer instance = new BizStringDeserializer();

    /**
     * 反序列化，处理空白字符串
     */
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        return super.deserialize(p,ctxt).trim();
    }
}
