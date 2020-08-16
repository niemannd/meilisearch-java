package com.github.niemannd.meilisearch.json;

public interface JsonProcessor {
    String serialize(Object o);
    <T> T deserialize(String o, Class<?> targetClass, Class<?>... parameters);
}
