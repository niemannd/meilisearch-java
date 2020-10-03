package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.request.HttpEntity;
import io.github.niemannd.meilisearch.http.request.HttpRequest;
import io.github.niemannd.meilisearch.http.response.HttpResponse;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * {@inheritDoc}
 * <p>
 * The GenericServiceTemplate first executes the supplied {@link HttpRequest} and then deserializes the response
 */
public class GenericServiceTemplate implements ServiceTemplate {
    private final HttpClient<?> client;
    private final JsonProcessor processor;

    /**
     * @param client    a {@link HttpClient}
     * @param processor a {@link JsonProcessor}
     */
    public GenericServiceTemplate(HttpClient<?> client, JsonProcessor processor) {
        this.client = client;
        this.processor = processor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpClient<?> getClient() {
        return client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonProcessor getProcessor() {
        return processor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(HttpRequest request, Class<?> targetClass, Class<?>... parameter) throws MeiliException {
        try {
            if (targetClass == null) {
                return (T) makeRequest(request);
            }
            return (T) CompletableFuture
                    .completedFuture(makeRequest(request))
                    .thenApply(httpResponse -> processor.deserialize(httpResponse.getContent(), targetClass, parameter))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new MeiliException(e);
        }
    }

    /**
     * Executes the given {@link HttpRequest}
     * @param request the {@link HttpRequest}
     * @return the HttpResponse
     * @throws MeiliException in case there are Problems with the Request, including error codes
     */
    private HttpResponse<?> makeRequest(HttpRequest request) throws MeiliException {
        switch (request.getMethod()) {
            case GET:
                return client.get(request.getPath(), request.getHeaders());
            case POST:
                return client.post(
                        request.getPath(),
                        (request instanceof HttpEntity ? ((HttpEntity<?>) request).getContent() : null)
                );
            case PUT:
                return client.put(
                        request.getPath(),
                        request.getHeaders(),
                        (request instanceof HttpEntity ? ((HttpEntity<?>) request).getContent() : null)
                );
            case DELETE:
                return client.delete(request.getPath());
            default:
                throw new MeiliException(new IllegalStateException("Unexpected value: " + request.getMethod()));
        }
    }
}
