package com.github.niemannd.meilisearch.api.documents;

import com.github.niemannd.meilisearch.api.MeiliErrorException;
import com.github.niemannd.meilisearch.config.Configuration;
import com.github.niemannd.meilisearch.http.HttpClient;
import com.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.Collections;
import java.util.List;

public class DocumentService<T> {

    private final HttpClient client;
    private final Configuration config;
    private final JsonProcessor jsonProcessor;
    private final String indexName;

    public DocumentService(String indexName, HttpClient client, Configuration config, JsonProcessor jsonProcessor) throws MeiliErrorException {
        this.indexName = indexName;
        this.client = client;
        this.config = config;
        this.jsonProcessor = jsonProcessor;
    }

    public T getDocument(String identifier) throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
        String body = client.get(requestQuery, Collections.emptyMap());
        return jsonProcessor.deserialize(body, config.mustGetDocumentType(indexName));
    }

    public List<T> getDocuments() throws MeiliErrorException {
        return getDocuments(20);
    }

    public List<T> getDocuments(int limit) throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/documents?limit=" + limit;
        String body = client.get(requestQuery, Collections.emptyMap());
        return jsonProcessor.deserialize(body, List.class, config.mustGetDocumentType(indexName));
    }

    public Update addDocument(String data) throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        String body = client.post(requestQuery, data);
        return jsonProcessor.deserialize(body, Update.class);
    }

    public Update addDocument(List<T> data) throws MeiliErrorException {
        String dataString = jsonProcessor.serialize(data);
        return addDocument(dataString);
    }

    public Update replaceDocument(String data) throws MeiliErrorException {
        return addDocument(data);
    }

    public Update replaceDocument(List<T> data) throws MeiliErrorException {
        String dataString = jsonProcessor.serialize(data);
        return replaceDocument(dataString);
    }

    public Update updateDocument(String data) throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        String body = client.put(requestQuery, Collections.emptyMap(), data);
        return jsonProcessor.deserialize(body, Update.class);
    }

    public boolean deleteDocument(String identifier) throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
        return client.delete(requestQuery);
    }

    public boolean deleteDocuments() throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return client.delete(requestQuery);
    }

    public SearchResponse<T> search(String q) throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/search";
        SearchRequest sr = new SearchRequest(q);
        return jsonProcessor.deserialize(client.post(requestQuery, sr), SearchResponse.class, config.mustGetDocumentType(indexName));
    }

    public SearchResponse<T> search(SearchRequest sr) throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/search";
        return jsonProcessor.deserialize(client.post(requestQuery, sr), SearchResponse.class, config.mustGetDocumentType(indexName));
    }

    public Update getUpdate(int updateId) throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/updates/" + updateId;
        return jsonProcessor.deserialize(client.get(requestQuery, Collections.emptyMap()), Update.class);
    }

    public List<Update> getUpdates() throws MeiliErrorException {
        String requestQuery = "/indexes/" + indexName + "/updates";
        return jsonProcessor.deserialize(client.get(requestQuery, Collections.emptyMap()), List.class, Update.class);
    }

}
