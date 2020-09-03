package io.github.niemannd.meilisearch.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.MeiliJSONException;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class JacksonJsonProcessor implements JsonProcessor {
    private static final Logger log = getLogger(JacksonJsonProcessor.class);

    private final ObjectMapper mapper;

    public JacksonJsonProcessor() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JacksonJsonProcessor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String serialize(Object o) throws MeiliException {
        if (o.getClass() == String.class) {
            return (String) o;
        }
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new MeiliJSONException("Error while serializing: ", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(Object o, Class<?> targetClass, Class<?>... parameters) throws MeiliException {
        if (o == null) {
            throw new MeiliJSONException("String to deserialize is null");
        }
        if (targetClass == String.class) {
            return (T) o;
        }
        try {
            if (parameters == null || parameters.length == 0) {
                return (T) mapper.readValue((String) o, targetClass);
            } else {
                return mapper.readValue((String) o, mapper.getTypeFactory().constructParametricType(targetClass, parameters));
            }
        } catch (IOException e) {
            throw new MeiliJSONException("Error while serializing: ", e);
        }
    }
}
