package io.github.niemannd.meilisearch.http.request;

import java.util.Map;

public interface HttpRequest {
    String getMethod();
    void setMethod(String method);
    String getPath();
    void setPath(String path);
    Map<String,String> getHeaders();
    void setHeaders(Map<String,String> headers);
}
