package com.oz.office_tastezip.global.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper;
    private static final ObjectMapper prettyMapper;
    private static final TypeReference<List<Object>> typeRefList = new TypeReference<>() {
    };
    private static final TypeReference<Map<String, Object>> typeRefMap = new TypeReference<>() {
    };

    private JsonUtils() {
    }

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModules(new JavaTimeModule(), new Jdk8Module());

        prettyMapper = new ObjectMapper();
        prettyMapper.enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModules(new JavaTimeModule(), new Jdk8Module());
    }

    public static String getJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Json serialize error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static <T> T getObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Json deserialize error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static <T> T getObject(Map<String, Object> dataMap, Class<T> clazz) {
        return objectMapper.convertValue(dataMap, clazz);
    }

    public static Map<String, Object> toMap(String json) {
        try {
            return objectMapper.readValue(json, typeRefMap);
        } catch (JsonProcessingException e) {
            log.error("Json toMap error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static Map<String, Object> toMap(Object obj) {
        return objectMapper.convertValue(obj, typeRefMap);
    }

    public static List<?> toList(String json) {
        try {
            return objectMapper.readValue(json, typeRefList);
        } catch (JsonProcessingException e) {
            log.error("Json toList error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static List<Object> toList(File jsonFile) {
        try {
            return objectMapper.readValue(jsonFile, typeRefList);
        } catch (IOException e) {
            log.error("Json file toList error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static List<?> toList(Object obj) {
        return objectMapper.convertValue(obj, typeRefList);
    }

    public static Map<String, Object> toMap(File jsonFile) {
        try {
            return objectMapper.readValue(jsonFile, typeRefMap);
        } catch (IOException e) {
            log.error("Json file toMap error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static Map<String, Object> toMap(InputStream jsonStream) {
        try {
            return objectMapper.readValue(jsonStream, typeRefMap);
        } catch (IOException e) {
            log.error("Json stream toMap error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static <T> T treeToValue(JsonNode jsonNode, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (IOException e) {
            log.error("Json treeToValue error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static String prettyPrint(String jsonString) {
        try {
            Object jsonValue;
            try {
                jsonValue = prettyMapper.readValue(jsonString, Map.class);
            } catch (JsonProcessingException ex) {
                jsonValue = prettyMapper.readValue(jsonString, Collection.class);
            }
            return prettyMapper.writeValueAsString(jsonValue);
        } catch (JsonProcessingException e) {
            log.error("prettyPrint error: {} - input: {}", e.getMessage(), jsonString);
            return "{}";
        }
    }

    public static boolean isJsonFormat(String payload) {
        try {
            if (StringUtils.isBlank(payload)) return false;
            JsonElement jsonElement = JsonParser.parseString(payload);
            return jsonElement != null && jsonElement.isJsonObject();
        } catch (Exception e) {
            return false;
        }
    }
}
