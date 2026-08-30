package com.ruoyi.framework.config;

import java.nio.charset.Charset;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Redis使用Jackson序列化
 */
public class JacksonJsonRedisSerializer<T> implements RedisSerializer<T>
{
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Class<T> clazz;

    public JacksonJsonRedisSerializer(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T value) throws SerializationException
    {
        if (value == null)
        {
            return new byte[0];
        }
        try
        {
            if (clazz != null && !Object.class.equals(clazz))
            {
                return MAPPER.writeValueAsString(value).getBytes(DEFAULT_CHARSET);
            }
            ObjectNode wrapper = MAPPER.createObjectNode();
            wrapper.put("@class", value.getClass().getName());
            wrapper.set("value", MAPPER.valueToTree(value));
            return MAPPER.writeValueAsString(wrapper).getBytes(DEFAULT_CHARSET);
        }
        catch (JacksonException e)
        {
            throw new SerializationException("Jackson serialize error", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException
    {
        if (bytes == null || bytes.length == 0)
        {
            return null;
        }
        try
        {
            String str = new String(bytes, DEFAULT_CHARSET);
            if (clazz != null && !Object.class.equals(clazz))
            {
                return MAPPER.readValue(str, clazz);
            }
            JsonNode root = MAPPER.readTree(str);
            if (root != null && root.isObject())
            {
                String className = root.path("@class").asText(null);
                if (className != null && !className.isEmpty())
                {
                    return (T) MAPPER.readValue(root.path("value").toString(), resolveClass(className));
                }
                className = root.path("@type").asText(null);
                if (className != null && !className.isEmpty())
                {
                    ObjectNode objectNode = (ObjectNode) root;
                    objectNode.remove("@type");
                    return (T) MAPPER.readValue(objectNode.toString(), resolveClass(className));
                }
            }
            return MAPPER.readValue(str, clazz);
        }
        catch (JacksonException e)
        {
            throw new SerializationException("Jackson deserialize error", e);
        }
    }

    private Class<?> resolveClass(String className)
    {
        if (!(className.startsWith("com.ruoyi.")
                || className.startsWith("java.lang.")
                || className.startsWith("java.util.")
                || className.startsWith("java.math.")
                || className.startsWith("java.time.")))
        {
            throw new SerializationException("Disallowed redis type: " + className);
        }
        try
        {
            return MAPPER.getTypeFactory().findClass(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new SerializationException("Unknown redis type: " + className, e);
        }
    }
}
