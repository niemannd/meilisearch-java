package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.apache.hc.core5.http.HttpRequest;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GenericServiceTemplate implements ServiceTemplate {
    private final HttpClient<?> client;
    private final JsonProcessor processor;

    public GenericServiceTemplate(HttpClient<?> client, JsonProcessor processor) {
        this.client = client;
        this.processor = processor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(HttpRequest request, Class<?> targetClass, Class<?>... parameter) throws MeiliException {
        try {
            return (T) CompletableFuture
                    .completedFuture(client.get("", Collections.emptyMap()))
                    .thenApply(httpResponse -> processor.deserialize(httpResponse.getContent(), targetClass, parameter))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new MeiliException(e);
        }
    }
}
