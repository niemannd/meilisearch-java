package io.github.niemannd.meilisearch.json;

import io.github.niemannd.meilisearch.api.MeiliException;

public interface JsonProcessor {
    String serialize(Object o) throws MeiliException;

    <T> T deserialize(String o, Class<?> targetClass, Class<?>... parameters) throws MeiliException;
}
