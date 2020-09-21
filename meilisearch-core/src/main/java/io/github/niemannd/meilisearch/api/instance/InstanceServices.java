package io.github.niemannd.meilisearch.api.instance;

import io.github.niemannd.meilisearch.ServiceTemplate;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpMethod;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InstanceServices {
    private final ServiceTemplate serviceTemplate;

    public InstanceServices(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    /**
     * @param maintenance true if maintenance should be enabled, false to disable maintenance
     * @return false in case of an error, otherwise true
     */
    public boolean setMaintenance(boolean maintenance) {
        try {
            String content = "{\"health\": " + (maintenance ? "true" : "false") + " }";
            serviceTemplate.execute(new BasicHttpRequest(HttpMethod.PUT, "/health", content), String.class);
            return true;
        } catch (MeiliException e) {
            return false;
        }
    }

    /**
     * @return true if everything is ok, false if meilisearch is in maintenance mode
     */
    public boolean isHealthy() {
        try {
            serviceTemplate.execute(new BasicHttpRequest(HttpMethod.GET, "/health"), null);
            return true;
        } catch (MeiliException e) {
            return false;
        }
    }

    /**
     * @return a map with version information of meilisearch
     */
    public Map<String, String> getVersion() {
        try {
            return serviceTemplate.execute(
                    new BasicHttpRequest(HttpMethod.GET, "/version"),
                    HashMap.class,
                    String.class,
                    String.class
            );
        } catch (MeiliException e) {
            return Collections.emptyMap();
        }
    }
}
