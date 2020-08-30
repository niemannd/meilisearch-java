package io.github.niemannd.meilisearch.api.instance;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.HttpResponse;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InstanceServices {
    private final HttpClient client;
    private final JsonProcessor processor;

    public InstanceServices(HttpClient client, JsonProcessor processor) {
        this.client = client;
        this.processor = processor;
    }

    public boolean setMaintenance(boolean maintenance) {
        try {
            client.put("/health", Collections.emptyMap(), Collections.singletonMap("health", maintenance));
            return true;
        } catch (MeiliException e) {
            return false;
        }
    }

    public boolean isHealthy() {
        try {
            client.get("/health", Collections.emptyMap());
            return true;
        } catch (MeiliException e) {
            return false;
        }
    }

    public Map<String, String> getVersion() {
        try {
            HttpResponse response = client.get("/version", Collections.emptyMap());
            return processor.deserialize(response.getContent(), HashMap.class, String.class, String.class);
        } catch (MeiliException e) {
            return Collections.emptyMap();
        }
    }
}
