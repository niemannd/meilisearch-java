package io.github.niemannd.meilisearch.http.request;

import java.io.IOException;

public interface HttpEntity<T> {
    T getContent() throws IOException, UnsupportedOperationException;
}
