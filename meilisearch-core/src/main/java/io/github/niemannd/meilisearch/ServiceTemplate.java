package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.request.HttpRequest;
import io.github.niemannd.meilisearch.http.response.HttpResponse;
import io.github.niemannd.meilisearch.json.JsonProcessor;

/**
 * A ServiceTemplate combines the HttpClient implementation with the JsonProcessor implementation and specifies how both work together.
 *
 */
public interface ServiceTemplate {

    /**
     *
     * @return the wrapped HttpClient implementation
     */
    HttpClient<?> getClient();

    /**
     *
     * @return the wrapped JsonProcessor implementation
     */
    JsonProcessor getProcessor();

    /**
     * Executes the given request and deserializes the response
     *
     * @param request the HttpRequest to execute
     * @param targetClass the Type of Object to deserialize
     * @param parameter in case targetClass is a generic, parameter contains the specific types for the generic
     * @param <T> type of targetClass or {@link HttpResponse} when targetClass is null
     * @return the deserialized response of type targetClass or the {@link HttpResponse} if targetClass is null
     * @throws MeiliException as a wrapper of API or JSON exceptions
     */
    <T> T execute(HttpRequest request, Class<?> targetClass, Class<?>... parameter) throws MeiliException;
}
