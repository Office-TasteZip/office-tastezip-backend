package com.oz.office_tastezip.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class JsonUtil {

    private static final ObjectMapper prettyMapper;
    private static final TypeReference<List<Object>> typeRefList;
    private static final TypeReference<Map<String, Object>> typeRefMap;
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {
    }

    static {
        prettyMapper = (new ObjectMapper()).enable(SerializationFeature.INDENT_OUTPUT);
        typeRefMap = new TypeReference<>() {
        };
        typeRefList = new TypeReference<>() {
        };
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
    }

    public static String getJson(Object _obj) {
        try {
            return mapper.writeValueAsString(_obj);
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static <T> T getObject(String _json, Class<T> _clazz) {
        try {
            return mapper.readValue(_json, _clazz);
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static <T> T getObject(Map<String, Object> dataMap, Class<T> _clazz) {
        return mapper.convertValue(dataMap, _clazz);
    }

    public static Map<String, Object> toMap(String _json) {
        try {
            return mapper.readValue(_json, typeRefMap);
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static Map<String, Object> toMap(Object _obj) {
        return mapper.convertValue(_obj, typeRefMap);
    }

    public static List<?> toList(String _json) {
        try {
            return mapper.readValue(_json, typeRefList);
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static List<Object> toList(File _jsonFile) {
        try {
            return mapper.readValue(_jsonFile, typeRefList);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static List<?> toList(Object _obj) {
        return mapper.convertValue(_obj, typeRefList);
    }

    public static Map<String, Object> toMap(File _jsonFile) {
        try {
            return mapper.readValue(_jsonFile, typeRefMap);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static Map<String, Object> toMap(InputStream _jsonStream) {
        try {
            return mapper.readValue(_jsonStream, typeRefMap);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static <T> T treeToValue(JsonNode _jsonNode, Class<T> _clazz) {
        try {
            return mapper.treeToValue(_jsonNode, _clazz);
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static String prettyPrint(String jsonString) {
        try {
            Object jsonValue;
            try {
                jsonValue = prettyMapper.readValue(jsonString, Map.class);
            } catch (JsonProcessingException var3) {
                jsonValue = prettyMapper.readValue(jsonString, Collection.class);
            }

            return prettyMapper.writeValueAsString(jsonValue);
        } catch (JsonProcessingException e) {
            log.error("{} - {}", e.getMessage(), jsonString);
            return "{}";
        }
    }

    public static boolean isJsonFormat(String payload) {
        try {
            if (StringUtils.isBlank(payload)) {
                return false;
            } else {
                JsonElement jsonElement = JsonParser.parseString(payload);
                return jsonElement != null && jsonElement.isJsonObject();
            }
        } catch (Exception var2) {
            return false;
        }
    }

}

