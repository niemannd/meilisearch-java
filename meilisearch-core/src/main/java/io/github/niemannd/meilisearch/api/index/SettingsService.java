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

    /**
     *
     * @param index the indexname
     * @return the index settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Settings getSettings(String index) throws MeiliException {
        return jsonProcessor.deserialize(
                client.get("/indexes/" + index + "/settings", Collections.emptyMap()).getContent(), Settings.class
        );
    }

    /**
     *
     * @param index the indexname
     * @param settings the new Settings
     * @return the updated settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update updateSettings(String index, Settings settings) throws MeiliException {
        return jsonProcessor.deserialize(client.post("/indexes/" + index + "/settings", settings).getContent(), Update.class);
    }

    /**
     *
     * @param index the indexname
     * @return the settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update resetSettings(String index) throws MeiliException {
        return jsonProcessor.deserialize(client.delete("/indexes/" + index + "/settings").getContent(), Update.class);
    }
}
