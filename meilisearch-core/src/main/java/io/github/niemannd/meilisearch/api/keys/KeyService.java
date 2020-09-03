package io.github.niemannd.meilisearch.api.keys;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeyService {
    private final JsonProcessor jsonProcessor;
    private final HttpClient<?> client;

    public KeyService(HttpClient<?> client, JsonProcessor jsonProcessor) {
        this.jsonProcessor = jsonProcessor;
        this.client = client;
    }

    public Map<String, String> get() throws MeiliException {
        String requestQuery = "/keys";
        return jsonProcessor.deserialize(client.get(requestQuery, Collections.emptyMap()).getContent(), HashMap.class, String.class, String.class);
    }
}
