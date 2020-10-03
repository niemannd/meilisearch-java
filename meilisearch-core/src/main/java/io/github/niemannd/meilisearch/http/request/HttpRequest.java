package io.github.niemannd.meilisearch.http.request;

import io.github.niemannd.meilisearch.http.HttpMethod;

import java.util.Map;

public interface HttpRequest {
    HttpMethod getMethod();
    void setMethod(HttpMethod method);
    String getPath();
    void setPath(String path);
    Map<String,String> getHeaders();
    void setHeaders(Map<String,String> headers);
}
