package com.github.niemannd.meilisearch.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigurationBuilder {
    private String url = "http://127.0.0.1:7700";
    private Supplier<String> key = () -> null;
    private final Map<String, Class<?>> documentTypes = new HashMap<>();

    public ConfigurationBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public ConfigurationBuilder setKey(Supplier<String> key) {
        this.key = key;
        return this;
    }

    public ConfigurationBuilder setDocumentTypes(Map<String, Class<?>> documentTypes) {
        this.documentTypes.putAll(documentTypes);
        return this;
    }

    public ConfigurationBuilder addDocumentType(String index, Class<?> type) {
        this.documentTypes.put(index, type);
        return this;
    }

    public Configuration build() {
        return new Configuration(this.url, this.key, this.documentTypes);
    }
}
