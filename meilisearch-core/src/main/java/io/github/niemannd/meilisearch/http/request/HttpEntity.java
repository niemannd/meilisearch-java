package io.github.niemannd.meilisearch.http.request;

public interface HttpEntity<T> {
    T getContent();
}
