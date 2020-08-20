package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

public class DocumentServiceFactory {
    public <T> DocumentService<T> createService(Class<T> clazz, String indexName, HttpClient client, Configuration config, JsonProcessor jsonProcessor) {
        try {
            return new DocumentService<>(indexName, client, config, jsonProcessor);
        } catch (ClassCastException e) {
            throw new MeiliException(e);
        }
    }
}
