package com.github.niemannd.meilisearch;

import com.github.niemannd.meilisearch.api.documents.DocumentService;
import com.github.niemannd.meilisearch.api.index.IndexService;
import com.github.niemannd.meilisearch.api.keys.KeyService;
import com.github.niemannd.meilisearch.config.Configuration;
import com.github.niemannd.meilisearch.http.HttpClient;
import com.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.HashMap;
import java.util.Map;

public class MeiliClient {
    private final Configuration config;

    private final IndexService indexService;
    private final KeyService keyService;

    private final HashMap<String, DocumentService<?>> documentServices = new HashMap<>();


    public MeiliClient(Configuration config, HttpClient client, JsonProcessor jsonProcessor) {
        this.config = config;
        this.indexService = new IndexService(client, jsonProcessor);
        this.keyService = new KeyService(client, jsonProcessor);

        Map<String, Class<?>> documentTypes = config.getDocumentTypes();
        for (String index : documentTypes.keySet()) {
            documentServices.put(
                    index,
                    createService(documentTypes.get(index), index, client, config, jsonProcessor)
            );
        }
    }

    public <T> DocumentService<T> createService(Class<T> clazz, String indexName, HttpClient client, Configuration config, JsonProcessor jsonProcessor) {
        try {
            return new DocumentService<>(indexName, client, config, jsonProcessor);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public IndexService index() {
        return indexService;
    }

    @SuppressWarnings("unchecked")
    public <T> DocumentService<T> documentServiceForIndex(String index) {
        return (DocumentService<T>) documentServices.get(index);
    }

    public KeyService keys() {
        return keyService;
    }

    public Configuration getConfig() {
        return config;
    }
}
