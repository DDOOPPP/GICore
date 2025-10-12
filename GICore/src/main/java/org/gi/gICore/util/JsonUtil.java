package org.gi.gICore.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.gi.gICore.GICore;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JsonUtil {
    private static JsonMapper mapper = new JsonMapper();
    private static ModuleLogger logger = new ModuleLogger(GICore.getInstance(),"JsonUtil");
    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static JsonMapper getMapper(){
        return mapper;
    }

    public static String toJson(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Json Convert Error: %s", e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        if (json == null || json.isEmpty()) return null;
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            logger.error(String.format("Json Convert Error: %s | input=%s", e.getMessage(), json));
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz){
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("Json Convert Error: %s", e.getMessage());
            return null;
        }
    }

    public static Set<String> getKeys(File file){
        try{
            Map<String ,String > keyMaps =  mapper.readValue(file, new TypeReference<Map<String,String>>() {
            });

            if (keyMaps == null) {
                return Set.of();
            }

            return keyMaps.keySet();
        } catch (StreamReadException e) {
            logger.error("Json Convert Error: %s", e.getMessage());
            return Set.of();
        } catch (DatabindException e) {
            logger.error("Databind Error: %s", e.getMessage());
            return Set.of();
        } catch (IOException e) {
            logger.error("IO Error: %s", e.getMessage());
            return Set.of();
        }
    }

    public static Map<String, String> getKeyValueMap(File file){
        try{
            Map<String ,String > keyMaps =  mapper.readValue(file, new TypeReference<Map<String,String>>() {
            });

            if (keyMaps == null) {
                return Map.of();
            }

            return keyMaps;
        } catch (StreamReadException e) {
            logger.error("Json Convert Error: %s", e.getMessage());
            return Map.of();
        } catch (DatabindException e) {
            logger.error("Databind Error: %s", e.getMessage());
            return Map.of();
        } catch (IOException e) {
            logger.error("IO Error: %s", e.getMessage());
            return Map.of();
        }
    }
}
