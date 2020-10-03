package io.github.niemannd.meilisearch.http.request;

import io.github.niemannd.meilisearch.ServiceTemplate;
import io.github.niemannd.meilisearch.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class BasicHttpRequestFactory implements HttpRequestFactory {

    private final ServiceTemplate serviceTemplate;

    public BasicHttpRequestFactory(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    @Override
    public <T> HttpRequest create(HttpMethod method, String path, T content, Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        String body = null;
        if (content instanceof String) {
            body = (String) content;
        } else if (content != null) {
            body = serviceTemplate.getProcessor().serialize(content);
        }

        return new BasicHttpRequest(method, path, body, headers);
    }
}
