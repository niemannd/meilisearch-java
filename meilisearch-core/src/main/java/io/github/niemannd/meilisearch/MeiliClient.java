package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.index.IndexService;
import io.github.niemannd.meilisearch.api.keys.KeyService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MeiliClient {
    private final Configuration config;

    private final IndexService indexService;
    private final KeyService keyService;

    private final HashMap<Class<?>, DocumentService<?>> documentServices = new HashMap<>();

    public MeiliClient(Configuration config, HttpClient client, JsonProcessor jsonProcessor, DocumentServiceFactory documentServiceFactory) {
        this.config = config;
        this.indexService = new IndexService(client, jsonProcessor);
        this.keyService = new KeyService(client, jsonProcessor);

        Map<String, Class<?>> documentTypes = config.getDocumentTypes();
        for (String index : documentTypes.keySet()) {
            documentServices.put(
                    documentTypes.get(index),
                    documentServiceFactory.createService(documentTypes.get(index), index, client, config, jsonProcessor)
            );
        }
    }

    public MeiliClient(Configuration config, HttpClient client, JsonProcessor jsonProcessor) {
        this(config, client, jsonProcessor, new DocumentServiceFactory());
    }

    public IndexService indexes() {
        return indexService;
    }

    @SuppressWarnings("unchecked")
    public <T> DocumentService<T> documents(Class<T> documentType) {
        return (DocumentService<T>) documentServices.get(documentType);
    }

    @SuppressWarnings("unchecked")
    public <T> DocumentService<T> documentServiceForIndex(String index) {
        Optional<Class<?>> documentType = config.getDocumentType(index);
        if (!documentType.isPresent()) {
            throw new MeiliException("documentType could not be found");
        }
        return (DocumentService<T>) documentServices.get(documentType.get());
    }

    public KeyService keys() {
        return keyService;
    }

    public Configuration getConfig() {
        return config;
    }
}
