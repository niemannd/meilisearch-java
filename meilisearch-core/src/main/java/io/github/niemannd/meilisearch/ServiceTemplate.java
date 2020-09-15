package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import org.apache.hc.core5.http.HttpRequest;

public interface ServiceTemplate {
    <T> T execute(HttpRequest request, Class<?> targetClass, Class<?>... parameter) throws MeiliException;
}
