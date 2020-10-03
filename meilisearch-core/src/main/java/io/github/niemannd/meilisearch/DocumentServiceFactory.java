package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.http.request.HttpRequestFactory;
import io.github.niemannd.meilisearch.json.JsonProcessor;

public class DocumentServiceFactory {
    /**
     * @param indexName       name of an index
     * @param config          Configuration
     * @param serviceTemplate instance of a {@link JsonProcessor}, e.g. {@link io.github.niemannd.meilisearch.json.JacksonJsonProcessor}
     * @return a DocumentService for the index
     */
    public <T> DocumentService<T> createService(String indexName, Configuration config, ServiceTemplate serviceTemplate, HttpRequestFactory requestFactory) {
        return new DocumentService<>(indexName, config, serviceTemplate, requestFactory);
    }
}
