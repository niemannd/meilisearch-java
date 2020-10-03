package io.github.niemannd.meilisearch.http.response;

import java.util.Map;

public interface HttpResponse<B> {
    Map<String, String> getHeaders();

    int getStatusCode();

    B getContent();
}
