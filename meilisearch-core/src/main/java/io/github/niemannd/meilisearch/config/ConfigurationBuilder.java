package io.github.niemannd.meilisearch.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigurationBuilder {
    private String url = "http://127.0.0.1:7700";
    private Supplier<String> keySupplier = () -> null;
    private final Map<String, Class<?>> documentTypes = new HashMap<>();

    public ConfigurationBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public ConfigurationBuilder setKeySupplier(Supplier<String> keySupplier) {
        this.keySupplier = keySupplier;
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
        return new Configuration(this.url, this.keySupplier, this.documentTypes);
    }
}
