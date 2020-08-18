package com.github.niemannd.meilisearch.json;

import com.github.niemannd.meilisearch.api.MeiliJSONException;

public interface JsonProcessor {
    String serialize(Object o) throws MeiliJSONException;

    <T> T deserialize(String o, Class<?> targetClass, Class<?>... parameters) throws MeiliJSONException;
}
