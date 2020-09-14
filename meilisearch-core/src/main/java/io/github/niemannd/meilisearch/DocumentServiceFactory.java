package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

public class DocumentServiceFactory {
    /**
     *
     * @param indexName name of an index
     * @param client instance of a {@link HttpClient}, e.g. {@link io.github.niemannd.meilisearch.http.ApacheHttpClient}
     * @param config Configuration
     * @param jsonProcessor instance of a {@link JsonProcessor}, e.g. {@link io.github.niemannd.meilisearch.json.JacksonJsonProcessor}
     * @return a DocumentService for the index
     */
    public <T> DocumentService<T> createService(String indexName, HttpClient<?> client, Configuration config, JsonProcessor jsonProcessor) {
        return new DocumentService<>(indexName, client, config, jsonProcessor);
    }
}
