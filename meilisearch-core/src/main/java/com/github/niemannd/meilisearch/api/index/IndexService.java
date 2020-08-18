package com.github.niemannd.meilisearch.api.index;

import com.github.niemannd.meilisearch.api.MeiliErrorException;
import com.github.niemannd.meilisearch.http.HttpClient;
import com.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.Collections;
import java.util.HashMap;

public class IndexService {

    private final HttpClient client;
    private final JsonProcessor jsonProcessor;

    public IndexService(HttpClient client, JsonProcessor jsonProcessor) throws MeiliErrorException {
        this.client = client;
        this.jsonProcessor = jsonProcessor;
    }

    public Index createIndex(String uid) throws MeiliErrorException {
        return this.createIndex(uid, null);
    }

    public Index createIndex(String uid, String primaryKey) throws MeiliErrorException {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        if (primaryKey != null)
            params.put("primaryKey", primaryKey);
        return jsonProcessor.deserialize(client.post("/indexes", params), Index.class);
    }

    public Index getIndex(String uid) throws MeiliErrorException {
        String requestQuery = "/indexes/" + uid;
        return jsonProcessor.deserialize(client.get(requestQuery, Collections.emptyMap()), Index.class);
    }

    public Index[] getAllIndexes() throws MeiliErrorException {
        return jsonProcessor.deserialize(client.get("/indexes", Collections.emptyMap()), Index[].class);
    }

    public Index updateIndex(String uid, String primaryKey) throws MeiliErrorException {
        String requestQuery = "/indexes/" + uid;
        HashMap<String, String> body = new HashMap<>();
        body.put("primaryKey", primaryKey);
        String primaryKey1 = client.put(requestQuery, Collections.emptyMap(), body);
        return jsonProcessor.deserialize(primaryKey1, Index.class);
    }

    public boolean deleteIndex(String uid) throws MeiliErrorException {
        String requestQuery = "/indexes/" + uid;
        return client.delete(requestQuery);
    }

}
