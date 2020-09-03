package io.github.niemannd.meilisearch.api.documents;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.Collections;
import java.util.List;

public class DocumentService<T> {

    private final HttpClient<?> client;
    private final Configuration config;
    private final JsonProcessor jsonProcessor;
    private final String indexName;

    public DocumentService(String indexName, HttpClient<?> client, Configuration config, JsonProcessor jsonProcessor) throws MeiliException {
        this.indexName = indexName;
        this.client = client;
        this.config = config;
        this.jsonProcessor = jsonProcessor;
    }

    public T getDocument(String identifier) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
        return jsonProcessor.deserialize(
                client.get(requestQuery, Collections.emptyMap()).getContent(),
                config.mustGetDocumentType(indexName)
        );
    }

    public List<T> getDocuments() throws MeiliException {
        return getDocuments(20);
    }

    public List<T> getDocuments(int limit) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents?limit=" + limit;
        return jsonProcessor.deserialize(
                client.get(requestQuery, Collections.emptyMap()).getContent(),
                List.class,
                config.mustGetDocumentType(indexName)
        );
    }

    public Update addDocument(String data) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return jsonProcessor.deserialize(
                client.post(requestQuery, data).getContent(),
                Update.class
        );
    }

    public Update addDocument(List<T> data) throws MeiliException {
        String dataString = jsonProcessor.serialize(data);
        return addDocument(dataString);
    }

    public Update replaceDocument(String data) throws MeiliException {
        return addDocument(data);
    }

    public Update replaceDocument(List<T> data) throws MeiliException {
        String dataString = jsonProcessor.serialize(data);
        return replaceDocument(dataString);
    }

    public Update updateDocument(String data) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return jsonProcessor.deserialize(
                client.put(requestQuery, Collections.emptyMap(), data).getContent(),
                Update.class
        );
    }

    public Update deleteDocument(String identifier) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
        return jsonProcessor.deserialize(client.delete(requestQuery).getContent(), Update.class);
    }

    public Update deleteDocuments() throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return jsonProcessor.deserialize(client.delete(requestQuery).getContent(), Update.class);
    }

    public SearchResponse<T> search(String q) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/search";
        SearchRequest sr = new SearchRequest(q);
        return jsonProcessor.deserialize(
                client.post(requestQuery, sr).getContent(),
                SearchResponse.class,
                config.mustGetDocumentType(indexName)
        );
    }

    public SearchResponse<T> search(SearchRequest sr) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/search";
        return jsonProcessor.deserialize(
                client.post(requestQuery, sr).getContent(),
                SearchResponse.class,
                config.mustGetDocumentType(indexName));
    }

    public Update getUpdate(int updateId) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/updates/" + updateId;
        return jsonProcessor.deserialize(
                client.get(requestQuery, Collections.emptyMap()).getContent(),
                Update.class
        );
    }

    public List<Update> getUpdates() throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/updates";
        return jsonProcessor.deserialize(
                client.get(requestQuery, Collections.emptyMap()).getContent(),
                List.class,
                Update.class
        );
    }

}
