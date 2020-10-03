package io.github.niemannd.meilisearch.http.request;

import io.github.niemannd.meilisearch.http.HttpMethod;

import java.util.Map;

public interface HttpRequestFactory {
    <T> HttpRequest create(HttpMethod method, String path, T content, Map<String, String> headers);
}
