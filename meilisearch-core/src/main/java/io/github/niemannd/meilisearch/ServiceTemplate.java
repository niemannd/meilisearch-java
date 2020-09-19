package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.request.HttpRequest;
import io.github.niemannd.meilisearch.json.JsonProcessor;

public interface ServiceTemplate {
    HttpClient<?> getClient();

    JsonProcessor getProcessor();

    <T> T execute(HttpRequest request, Class<?> targetClass, Class<?>... parameter) throws MeiliException;
}
