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

    /**
     *
     * @param identifier the identifier of the document you are looking for
     * @return the Document as the
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public T getDocument(String identifier) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
        return jsonProcessor.deserialize(
                client.get(requestQuery, Collections.emptyMap()).getContent(),
                config.mustGetDocumentType(indexName)
        );
    }

    /**
     *
     * @return a list of Documents from the index.
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public List<T> getDocuments() throws MeiliException {
        return getDocuments(20);
    }

    /**
     *
     * @param limit maximum number of documents to be returned
     * @return a list of Documents from the index.
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public List<T> getDocuments(int limit) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents?limit=" + limit;
        return jsonProcessor.deserialize(
                client.get(requestQuery, Collections.emptyMap()).getContent(),
                List.class,
                config.mustGetDocumentType(indexName)
        );
    }

    /**
     *
     * @param data an already serialized document
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update addDocument(String data) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return jsonProcessor.deserialize(
                client.post(requestQuery, data).getContent(),
                Update.class
        );
    }

    /**
     *
     * @param data a list of document objects
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update addDocument(List<T> data) throws MeiliException {
        String dataString = jsonProcessor.serialize(data);
        return addDocument(dataString);
    }

    /**
     *
     * @param data the serialized document
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update replaceDocument(String data) throws MeiliException {
        return addDocument(data);
    }

    /**
     *
     * @param data a list of document objects
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update replaceDocument(List<T> data) throws MeiliException {
        String dataString = jsonProcessor.serialize(data);
        return replaceDocument(dataString);
    }

    /**
     *
     * @param data the serialized document
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update updateDocument(String data) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return jsonProcessor.deserialize(
                client.put(requestQuery, Collections.emptyMap(), data).getContent(),
                Update.class
        );
    }

    /**
     *
     * @param identifier the id of the document
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update deleteDocument(String identifier) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
        return jsonProcessor.deserialize(client.delete(requestQuery).getContent(), Update.class);
    }

    /**
     *
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update deleteDocuments() throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return jsonProcessor.deserialize(client.delete(requestQuery).getContent(), Update.class);
    }

    /**
     *
     * @param q the Querystring
     * @return a SearchResponse with the Hits represented by the mapped Class for this index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public SearchResponse<T> search(String q) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/search";
        SearchRequest sr = new SearchRequest(q);
        return jsonProcessor.deserialize(
                client.post(requestQuery, sr).getContent(),
                SearchResponse.class,
                config.mustGetDocumentType(indexName)
        );
    }

    /**
     *
     * @param sr SearchRequest
     * @return a SearchResponse with the Hits represented by the mapped Class for this index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public SearchResponse<T> search(SearchRequest sr) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/search";
        return jsonProcessor.deserialize(
                client.post(requestQuery, sr).getContent(),
                SearchResponse.class,
                config.mustGetDocumentType(indexName));
    }

    /**
     *
     * @param updateId the updateId
     * @return the update belonging to the updateID
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update getUpdate(int updateId) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/updates/" + updateId;
        return jsonProcessor.deserialize(
                client.get(requestQuery, Collections.emptyMap()).getContent(),
                Update.class
        );
    }

    /**
     *
     * @return a List of Updates
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public List<Update> getUpdates() throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/updates";
        return jsonProcessor.deserialize(
                client.get(requestQuery, Collections.emptyMap()).getContent(),
                List.class,
                Update.class
        );
    }

}
