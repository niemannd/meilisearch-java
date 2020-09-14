package io.github.niemannd.meilisearch.api.instance;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InstanceServices {
    private final HttpClient<?> client;
    private final JsonProcessor processor;

    public InstanceServices(HttpClient<?> client, JsonProcessor processor) {
        this.client = client;
        this.processor = processor;
    }

    /**
     *
     * @param maintenance true if maintenance should be enabled, false to disable maintenance
     * @return false in case of an error, otherwise true
     */
    public boolean setMaintenance(boolean maintenance) {
        try {
            client.put("/health", Collections.emptyMap(), Collections.singletonMap("health", maintenance));
            return true;
        } catch (MeiliException e) {
            return false;
        }
    }

    /**
     *
     * @return true if everything is ok, false if meilisearch is in maintenance mode
     */
    public boolean isHealthy() {
        try {
            client.get("/health", Collections.emptyMap());
            return true;
        } catch (MeiliException e) {
            return false;
        }
    }

    /**
     *
     * @return a map with version information of meilisearch
     */
    public Map<String, String> getVersion() {
        try {
            return processor.deserialize(
                    client.get("/version", Collections.emptyMap()).getContent(),
                    HashMap.class,
                    String.class,
                    String.class
            );
        } catch (MeiliException e) {
            return Collections.emptyMap();
        }
    }
}
