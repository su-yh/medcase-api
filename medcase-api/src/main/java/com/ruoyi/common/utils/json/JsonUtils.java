package com.ruoyi.common.utils.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.type.CollectionType;

/**
 * Jackson JSON工具类
 */
public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper().rebuild()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        .defaultTimeZone(TimeZone.getTimeZone("GMT+8"))
        .build();

    private JsonUtils() {

    }

    public static String toJSONString(Object value) {

        return toJSONString(value, Set.of());
    }

    public static String toJSONString(Object value, Collection<String> excludes) {

        try {

            JsonNode node = MAPPER.valueToTree(value);
            prune(node, excludes == null ? Set.of() : new HashSet<>(excludes));
            return MAPPER.writeValueAsString(node);
        }
        catch (JacksonException e) {

            throw new IllegalStateException("JSON serialization failed", e);
        }
    }

    public static JsonNode readTree(String json) {

        try {

            return MAPPER.readTree(json);
        }
        catch (JacksonException e) {

            throw new IllegalStateException("JSON parse failed", e);
        }
    }

    public static JsonNode toTree(Object value) {

        return MAPPER.valueToTree(value);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {

        try {

            return MAPPER.readValue(json, clazz);
        }
        catch (JacksonException e) {

            throw new IllegalStateException("JSON parse failed", e);
        }
    }

    public static <T> List<T> parseArray(String json, Class<T> elementType) {

        try {

            CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, elementType);
            return MAPPER.readValue(json, collectionType);
        }
        catch (JacksonException e) {

            throw new IllegalStateException("JSON parse failed", e);
        }
    }

    private static void prune(JsonNode node, Set<String> excludes) {

        if (node == null || excludes.isEmpty()) {

            return;
        }
        if (node.isObject()) {

            ObjectNode objectNode = (ObjectNode) node;
            List<String> removeKeys = new ArrayList<>();
            for (java.util.Map.Entry<String, JsonNode> field : objectNode.properties()) {

                if (excludes.contains(field.getKey())) {

                    removeKeys.add(field.getKey());
                }
                else {

                    prune(field.getValue(), excludes);
                }
            }
            removeKeys.forEach(objectNode::remove);
        }
        else if (node.isArray()) {

            for (JsonNode child : node) {

                prune(child, excludes);
            }
        }
    }
}
