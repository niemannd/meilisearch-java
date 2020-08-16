package com.github.niemannd.meilisearch.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.niemannd.meilisearch.http.ApacheHttpClient;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class JacksonJsonProcessor implements JsonProcessor {
    private static final Logger log = getLogger(JacksonJsonProcessor.class);

    private final ObjectMapper mapper;

    public JacksonJsonProcessor() {
        this(new ObjectMapper());
    }

    public JacksonJsonProcessor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String serialize(Object o) {
        if (o.getClass() == String.class) {
            return (String) o;
        }
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Error while serializing: ", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(String o, Class<?> targetClass, Class<?>... parameters) {
        if (targetClass == String.class) {
            return (T) o;
        }
        try {
            if (parameters == null || parameters.length == 0) {
                return (T) mapper.readValue(o, targetClass);
            } else {
                if (o != null)
                    return mapper.readValue(o, mapper.getTypeFactory().constructParametricType(targetClass, parameters));
            }
        } catch (IOException e) {
            log.error("Error while deserializing: ", e);
        }
        return null;
    }
}
