package io.github.niemannd.meilisearch.json;

import io.github.niemannd.meilisearch.api.MeiliJSONException;

public interface JsonProcessor {
    String serialize(Object o) throws MeiliJSONException;

    <T> T deserialize(String o, Class<?> targetClass, Class<?>... parameters) throws MeiliJSONException;
}
