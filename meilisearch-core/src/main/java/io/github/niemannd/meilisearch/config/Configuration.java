package io.github.niemannd.meilisearch.config;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Configuration {
    private final String url;
    private final Supplier<String> keySupplier;
    private final Map<String, Class<?>> documentTypes;

    public Configuration(String url, Supplier<String> keySupplier, Map<String, Class<?>> documentTypes) {
        this.url = url;
        this.keySupplier = keySupplier;
        this.documentTypes = documentTypes;
    }

    public String getUrl() {
        return url;
    }

    public Supplier<String> getKeySupplier() {
        return keySupplier;
    }

    /**
     *
     * @return the indexname-to-class mapping in an unmodifiable map
     */
    public Map<String, Class<?>> getDocumentTypes() {
        return Collections.unmodifiableMap(this.documentTypes);
    }

    /**
     *
     * @param index the index name as String
     * @return an Optional<CLass<?>> of resolved Class
     */
    public Optional<Class<?>> getDocumentType(String index) {
        return Optional.ofNullable(documentTypes.get(index));
    }

    /**
     *
     * @param index the index name as String
     * @return the mapped class
     * @throws DocumentTypeNotFoundException in case there is no mapping for the index
     */
    public Class<?> mustGetDocumentType(String index) {
        if (!documentTypes.containsKey(index)) {
            throw new DocumentTypeNotFoundException(index);
        }
        return documentTypes.get(index);
    }
}
