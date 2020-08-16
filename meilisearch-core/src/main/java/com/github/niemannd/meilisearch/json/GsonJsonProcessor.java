package com.github.niemannd.meilisearch.json;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class GsonJsonProcessor implements JsonProcessor {
    private static final Logger log = getLogger(GsonJsonProcessor.class);
    private final Gson gson;

    public GsonJsonProcessor() {
        this(new Gson());
    }

    public GsonJsonProcessor(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String serialize(Object o) {
        if (o.getClass() == String.class) {
            return (String) o;
        }
        try {
            return gson.toJson(o);
        } catch (JsonParseException e) {
            log.error("Error while serializing: ", e);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String o, Class<?> targetClass, Class<?>... parameters) {
        if (targetClass == String.class) {
            return (T) o;
        }
        try {
            if (parameters == null || parameters.length == 0) {
                return (T) gson.fromJson(o, targetClass);
            } else {
                Type[] types = Arrays.stream(parameters).map(aClass -> (Type) aClass).toArray(Type[]::new);
                return gson.fromJson(o, TypeToken.getParameterized(targetClass, types).getType());
            }
        } catch (JsonParseException e) {
            log.error("Error while deserializing: ", e);
        }
        return null;
    }
}
