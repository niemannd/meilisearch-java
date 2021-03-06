package io.github.niemannd.meilisearch.api.keys;

import io.github.niemannd.meilisearch.ServiceTemplate;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpMethod;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequest;
import io.github.niemannd.meilisearch.http.request.HttpRequestFactory;

import java.util.HashMap;
import java.util.Map;

public class KeyService {
    private final ServiceTemplate serviceTemplate;
    private final HttpRequestFactory requestFactory;

    public KeyService(ServiceTemplate serviceTemplate, HttpRequestFactory requestFactory) {
        this.serviceTemplate = serviceTemplate;
        this.requestFactory = requestFactory;
    }

    /**
     * @return the public and private keys in a map
     * @throws MeiliException in case some error happens
     */
    public Map<String, String> get() throws MeiliException {
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.GET, "/keys"),
                HashMap.class, String.class, String.class
        );
    }
}
