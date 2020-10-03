package io.github.niemannd.meilisearch.api.documents;

import io.github.niemannd.meilisearch.ServiceTemplate;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.http.HttpMethod;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequest;
import io.github.niemannd.meilisearch.http.request.HttpRequestFactory;

import java.util.List;

public class DocumentService<T> {

    private final ServiceTemplate serviceTemplate;
    private final HttpRequestFactory requestFactory;
    private final String indexName;
    private final Class<?> indexModel;

    public DocumentService(String indexName, Configuration config, ServiceTemplate serviceTemplate, HttpRequestFactory requestFactory) throws MeiliException {
        this.indexName = indexName;
        this.requestFactory = requestFactory;
        this.indexModel = config.mustGetDocumentType(indexName);
        this.serviceTemplate = serviceTemplate;
    }

    /**
     * @param identifier the identifier of the document you are looking for
     * @return the Document as the
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public T getDocument(String identifier) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.GET, requestQuery),
                indexModel
        );
    }

    /**
     * @return a list of Documents from the index.
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public List<T> getDocuments() throws MeiliException {
        return getDocuments(20);
    }

    /**
     * @param limit maximum number of documents to be returned
     * @return a list of Documents from the index.
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public List<T> getDocuments(int limit) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents?limit=" + limit;
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.GET, requestQuery),
                List.class,
                indexModel
        );
    }

    /**
     * @param data an already serialized document
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update addDocument(String data) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.POST, requestQuery, data),
                Update.class
        );
    }

    /**
     * @param data a list of document objects
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update addDocument(List<T> data) throws MeiliException {
        String dataString = serviceTemplate.getProcessor().serialize(data);
        return addDocument(dataString);
    }

    /**
     * @param data the serialized document
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update replaceDocument(String data) throws MeiliException {
        return addDocument(data);
    }

    /**
     * @param data a list of document objects
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update replaceDocument(List<T> data) throws MeiliException {
        String dataString = serviceTemplate.getProcessor().serialize(data);
        return replaceDocument(dataString);
    }

    /**
     * @param data the serialized document
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update updateDocument(String data) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.PUT, requestQuery, data),
                Update.class
        );
    }

    /**
     * @param identifier the id of the document
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update deleteDocument(String identifier) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.DELETE, requestQuery),
                Update.class
        );
    }

    /**
     * @return an Update object with the updateId
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update deleteDocuments() throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/documents";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.DELETE, requestQuery),
                Update.class
        );
    }

    /**
     * @param q the Querystring
     * @return a SearchResponse with the Hits represented by the mapped Class for this index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public SearchResponse<T> search(String q) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/search";
        SearchRequest sr = new SearchRequest(q);
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.POST, requestQuery, serviceTemplate.getProcessor().serialize(sr)),
                SearchResponse.class,
                indexModel
        );
    }

    /**
     * @param sr SearchRequest
     * @return a SearchResponse with the Hits represented by the mapped Class for this index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public SearchResponse<T> search(SearchRequest sr) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/search";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.POST, requestQuery, serviceTemplate.getProcessor().serialize(sr)),
                SearchResponse.class,
                indexModel
        );
    }

    /**
     * @param updateId the updateId
     * @return the update belonging to the updateID
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update getUpdate(int updateId) throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/updates/" + updateId;
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.GET, requestQuery),
                Update.class
        );
    }

    /**
     * @return a List of Updates
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public List<Update> getUpdates() throws MeiliException {
        String requestQuery = "/indexes/" + indexName + "/updates";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.GET, requestQuery),
                List.class,
                Update.class
        );
    }

}
