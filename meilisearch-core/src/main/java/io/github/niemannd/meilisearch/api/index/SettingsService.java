package io.github.niemannd.meilisearch.api.index;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.Update;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.Collections;

public class SettingsService {

    private final HttpClient<?> client;
    private final JsonProcessor jsonProcessor;

    public SettingsService(HttpClient<?> client, JsonProcessor jsonProcessor) {
        this.client = client;
        this.jsonProcessor = jsonProcessor;
    }

    public Settings getSettings(String index) throws MeiliException {
        return jsonProcessor.deserialize(
                client.get("/indexes/" + index + "/settings", Collections.emptyMap()).getContent(), Settings.class
        );
    }

    public Update updateSettings(String index, Settings settings) throws MeiliException {
        return jsonProcessor.deserialize(client.post("/indexes/" + index + "/settings", settings).getContent(), Update.class);
    }

    public Update resetSettings(String index) throws MeiliException {
        return jsonProcessor.deserialize(client.delete("/indexes/" + index + "/settings").getContent(), Update.class);
    }
}
