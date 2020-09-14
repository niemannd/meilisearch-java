package io.github.niemannd.meilisearch.api.index;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.Update;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.Collections;
import java.util.HashMap;

public class IndexService {

    private final HttpClient<?> client;
    private final JsonProcessor jsonProcessor;
    private final SettingsService settingsService;

    public IndexService(HttpClient<?> client, JsonProcessor jsonProcessor, SettingsService settingsService) {
        this.client = client;
        this.jsonProcessor = jsonProcessor;
        this.settingsService = settingsService;
    }

    public IndexService(HttpClient<?> client, JsonProcessor jsonProcessor) throws MeiliException {
        this(client, jsonProcessor, new SettingsService(client, jsonProcessor));
    }

    /**
     *
     * @param uid the uid of the index to be created
     * @return an {@link Index} Object
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index createIndex(String uid) throws MeiliException {
        return this.createIndex(uid, null);
    }

    /**
     *
     * @param uid the indexname
     * @param primaryKey the primaryKey for that index
     * @return the newly created index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index createIndex(String uid, String primaryKey) throws MeiliException {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        if (primaryKey != null)
            params.put("primaryKey", primaryKey);
        return jsonProcessor.deserialize(
                client.post("/indexes", params).getContent(),
                Index.class
        );
    }

    /**
     *
     * @param uid the indexname
     * @return the index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index getIndex(String uid) throws MeiliException {
        String requestQuery = "/indexes/" + uid;
        return jsonProcessor.deserialize(client.get(requestQuery, Collections.emptyMap()).getContent(), Index.class);
    }

    /**
     *
     * @return an array of all indexes
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index[] getAllIndexes() throws MeiliException {
        return jsonProcessor.deserialize(client.get("/indexes", Collections.emptyMap()).getContent(), Index[].class);
    }

    /**
     *
     * @param uid the indexname
     * @param primaryKey the primaryKey for that index
     * @return the updated index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index updateIndex(String uid, String primaryKey) throws MeiliException {
        String requestQuery = "/indexes/" + uid;
        HashMap<String, String> body = new HashMap<>();
        body.put("primaryKey", primaryKey);
        return jsonProcessor.deserialize(
                client.put(requestQuery, Collections.emptyMap(), body).getContent(),
                Index.class
        );
    }

    /**
     *
     * @param uid the indexname
     * @return true if the index was deleted, otherwise false
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public boolean deleteIndex(String uid) throws MeiliException {
        String requestQuery = "/indexes/" + uid;
        return client.delete(requestQuery).getStatusCode() == 204;
    }

    /**
     *
     * @param index the indexname
     * @return the index settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Settings getSettings(String index) throws MeiliException {
        return settingsService.getSettings(index);
    }

    /**
     *
     * @param index the indexname
     * @param settings the new Settings
     * @return the updated settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update updateSettings(String index, Settings settings) throws MeiliException {
        return settingsService.updateSettings(index, settings);
    }

    /**
     *
     * @param index the indexname
     * @return the settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update resetSettings(String index) throws MeiliException {
        return settingsService.resetSettings(index);
    }
}
