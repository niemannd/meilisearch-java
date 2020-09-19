package io.github.niemannd.meilisearch.api.index;

import io.github.niemannd.meilisearch.ServiceTemplate;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.Update;
import io.github.niemannd.meilisearch.http.HttpMethod;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequest;

public class SettingsService {

    private final ServiceTemplate serviceTemplate;

    public SettingsService(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    /**
     * @param index the indexname
     * @return the index settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Settings getSettings(String index) throws MeiliException {
        String requestQuery = "/indexes/" + index + "/settings";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.GET, requestQuery),
                Settings.class
        );
    }

    /**
     * @param index    the indexname
     * @param settings the new Settings
     * @return the updated settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update updateSettings(String index, Settings settings) throws MeiliException {
        String requestQuery = "/indexes/" + index + "/settings";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.POST, requestQuery),
                Update.class
        );
    }

    /**
     * @param index the indexname
     * @return the settings
     * @throws MeiliException in case something went wrong (http error, json exceptions, etc)
     */
    public Update resetSettings(String index) throws MeiliException {
        String requestQuery = "/indexes/" + index + "/settings";
        return serviceTemplate.execute(
                new BasicHttpRequest(HttpMethod.DELETE, requestQuery),
                Update.class
        );
    }
}
