package io.github.niemannd.meilisearch.api.index;

import io.github.niemannd.meilisearch.ServiceTemplate;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.Update;
import io.github.niemannd.meilisearch.http.HttpMethod;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequest;
import io.github.niemannd.meilisearch.http.request.HttpRequestFactory;
import io.github.niemannd.meilisearch.http.response.HttpResponse;

import java.util.HashMap;

public class IndexService {

    private final ServiceTemplate serviceTemplate;
    private final HttpRequestFactory requestFactory;
    private final SettingsService settingsService;

    public IndexService(ServiceTemplate serviceTemplate, HttpRequestFactory requestFactory, SettingsService settingsService) {
        this.serviceTemplate = serviceTemplate;
        this.requestFactory = requestFactory;
        this.settingsService = settingsService;
    }

    public IndexService(ServiceTemplate serviceTemplate, HttpRequestFactory requestFactory) throws MeiliException {
        this(serviceTemplate, requestFactory, new SettingsService(serviceTemplate, requestFactory));
    }

    /**
     * @param uid the uid of the index to be created
     * @return an {@link Index} Object
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index createIndex(String uid) throws MeiliException {
        return this.createIndex(uid, null);
    }

    /**
     * @param uid        the indexname
     * @param primaryKey the primaryKey for that index
     * @return the newly created index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index createIndex(String uid, String primaryKey) throws MeiliException {
        HashMap<String, String> body = new HashMap<>();
        body.put("uid", uid);
        if (primaryKey != null)
            body.put("primaryKey", primaryKey);
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.POST, "/indexes",serviceTemplate.getProcessor().serialize(body)),
                Index.class
        );
    }

    /**
     * @param uid the indexname
     * @return the index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index getIndex(String uid) throws MeiliException {
        String requestQuery = "/indexes/" + uid;
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.GET, requestQuery),
                Index.class
        );
    }

    /**
     * @return an array of all indexes
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index[] getAllIndexes() throws MeiliException {
        String requestQuery = "/indexes";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.GET, requestQuery),
                Index[].class
        );
    }

    /**
     * @param uid        the indexname
     * @param primaryKey the primaryKey for that index
     * @return the updated index
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Index updateIndex(String uid, String primaryKey) throws MeiliException {
        String requestQuery = "/indexes/" + uid;
        HashMap<String, String> body = new HashMap<>();
        body.put("primaryKey", primaryKey);
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.PUT, requestQuery, serviceTemplate.getProcessor().serialize(body)),
                Index.class
        );
    }

    /**
     * @param uid the indexname
     * @return true if the index was deleted, otherwise false
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public boolean deleteIndex(String uid) throws MeiliException {
        String requestQuery = "/indexes/" + uid;
        return ((HttpResponse<?>) serviceTemplate.execute(new BasicHttpRequest(HttpMethod.DELETE, requestQuery), null)).getStatusCode() == 204;
    }

    /**
     * @param index the indexname
     * @return the index settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Settings getSettings(String index) throws MeiliException {
        return settingsService.getSettings(index);
    }

    /**
     * @param index    the indexname
     * @param settings the new Settings
     * @return the updated settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update updateSettings(String index, Settings settings) throws MeiliException {
        return settingsService.updateSettings(index, settings);
    }

    /**
     * @param index the indexname
     * @return the settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update resetSettings(String index) throws MeiliException {
        return settingsService.resetSettings(index);
    }
}
