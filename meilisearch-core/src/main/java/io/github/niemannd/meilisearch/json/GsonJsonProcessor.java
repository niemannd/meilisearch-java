package io.github.niemannd.meilisearch.json;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.MeiliJSONException;

public class GsonJsonProcessor implements JsonProcessor {
    private final Gson gson;

    public GsonJsonProcessor() {
        this(new Gson());
    }

    public GsonJsonProcessor(Gson gson) {
        this.gson = gson;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(Object o) throws MeiliException {
        if (o.getClass() == String.class) {
            return (String) o;
        }
        try {
            return gson.toJson(o);
        } catch (JsonParseException e) {
            throw new MeiliJSONException("Error while serializing: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Object o, Class<?> targetClass, Class<?>... parameters) throws MeiliException {
        if (o == null) {
            throw new MeiliJSONException("String to deserialize is null");
        }
        if (targetClass == String.class) {
            return (T) o;
        }
        try {
            if (parameters == null || parameters.length == 0) {
                return (T) gson.fromJson((String) o, targetClass);
            } else {
                TypeToken<?> parameterized = TypeToken.getParameterized(targetClass, parameters);
                return gson.fromJson((String) o, parameterized.getType());
            }
        } catch (JsonParseException e) {
            throw new MeiliJSONException("Error while deserializing: ", e);
        }
    }
}
