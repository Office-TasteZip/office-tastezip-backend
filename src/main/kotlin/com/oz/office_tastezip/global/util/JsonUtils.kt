package com.oz.office_tastezip.global.util

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.gson.JsonParser
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.InputStream

object JsonUtils {

    private val log = KotlinLogging.logger {}

    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        registerModules(JavaTimeModule(), Jdk8Module())
    }

    private val prettyMapper: ObjectMapper = ObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        registerModules(JavaTimeModule(), Jdk8Module())
    }

    private val typeRefList = object : TypeReference<List<Any>>() {}
    private val typeRefMap = object : TypeReference<Map<String, Any>>() {}

    fun getJson(obj: Any?): String? = try {
        objectMapper.writeValueAsString(obj)
    } catch (e: JsonProcessingException) {
        log.error(e) { "Json serialize error" }
        null
    }

    fun <T> getObject(json: String, clazz: Class<T>): T? = try {
        objectMapper.readValue(json, clazz)
    } catch (e: JsonProcessingException) {
        log.error(e) { "Json deserialize error" }
        null
    }

    fun <T> getObject(dataMap: Map<String, Any>, clazz: Class<T>): T =
        objectMapper.convertValue(dataMap, clazz)

    fun toMap(json: String): Map<String, Any>? = try {
        objectMapper.readValue(json, typeRefMap)
    } catch (e: JsonProcessingException) {
        log.error(e) { "Json toMap error" }
        null
    }

    fun toMap(obj: Any): Map<String, Any> = objectMapper.convertValue(obj, typeRefMap)

    fun toList(json: String): List<Any>? = try {
        objectMapper.readValue(json, typeRefList)
    } catch (e: JsonProcessingException) {
        log.error(e) { "Json toList error" }
        null
    }

    fun toList(jsonFile: File): List<Any>? = try {
        objectMapper.readValue(jsonFile, typeRefList)
    } catch (e: Exception) {
        log.error(e) { "Json file toList error" }
        null
    }

    fun toList(obj: Any): List<Any> = objectMapper.convertValue(obj, typeRefList)

    fun toMap(jsonFile: File): Map<String, Any>? = try {
        objectMapper.readValue(jsonFile, typeRefMap)
    } catch (e: Exception) {
        log.error(e) { "Json file toMap error" }
        null
    }

    fun toMap(jsonStream: InputStream): Map<String, Any>? = try {
        objectMapper.readValue(jsonStream, typeRefMap)
    } catch (e: Exception) {
        log.error(e) { "Json stream toMap error" }
        null
    }

    fun <T> treeToValue(jsonNode: JsonNode, clazz: Class<T>): T? = try {
        objectMapper.treeToValue(jsonNode, clazz)
    } catch (e: Exception) {
        log.error(e) { "Json treeToValue error" }
        null
    }

    fun prettyPrint(jsonString: String): String {
        return try {
            val jsonValue = try {
                prettyMapper.readValue(jsonString, Map::class.java)
            } catch (ex: JsonProcessingException) {
                prettyMapper.readValue(jsonString, Collection::class.java)
            }
            prettyMapper.writeValueAsString(jsonValue)
        } catch (e: Exception) {
            log.error(e) { "prettyPrint error - input: $jsonString" }
            "{}"
        }
    }

}
