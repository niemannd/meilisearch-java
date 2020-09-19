package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.index.IndexService;
import io.github.niemannd.meilisearch.api.instance.InstanceServices;
import io.github.niemannd.meilisearch.api.keys.KeyService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Basic Meilisearch Client
 */
public class MeiliClient {
    private final Configuration config;
    private final ServiceTemplate serviceTemplate;

    private final IndexService indexService;
    private final KeyService keyService;
    private final InstanceServices instanceServices;

    private final HashMap<Class<?>, DocumentService<?>> documentServices = new HashMap<>();

    /**
     * Creates a MeilisearchClient for the given {@code config}, {@code client},{@code jsonProcessor} and {@code documentServiceFactory}.
     *
     * @param config Configuration created by using a {@link io.github.niemannd.meilisearch.config.ConfigurationBuilder}
     * @param client an HTTPClient, e.g. {@link io.github.niemannd.meilisearch.http.ApacheHttpClient}
     * @param jsonProcessor an JsonProcessor , e.g. {@link io.github.niemannd.meilisearch.json.JacksonJsonProcessor}
     * @param documentServiceFactory DocumentServiceFactory for ServiceCreation
     */
    public MeiliClient(Configuration config, HttpClient<?> client, JsonProcessor jsonProcessor, DocumentServiceFactory documentServiceFactory) {
        this.config = config;
        this.serviceTemplate = new GenericServiceTemplate(client,jsonProcessor);
        this.indexService = new IndexService(serviceTemplate);
        this.keyService = new KeyService(serviceTemplate);
        this.instanceServices = new InstanceServices(serviceTemplate);

        Map<String, Class<?>> documentTypes = config.getDocumentTypes();
        for (String index : documentTypes.keySet()) {
            documentServices.put(
                    documentTypes.get(index),
                    documentServiceFactory.createService(index, config, serviceTemplate)
            );
        }
    }

    /**
     * Creates a MeilisearchClient for the given {@code config}, {@code client},{@code jsonProcessor}.
     * Uses the default {@link DocumentServiceFactory} for ServiceCreation
     *
     * @param config Configuration created by using a {@link io.github.niemannd.meilisearch.config.ConfigurationBuilder}
     * @param client an HTTPClient, e.g. {@link io.github.niemannd.meilisearch.http.ApacheHttpClient}
     * @param jsonProcessor an JsonProcessor , e.g. {@link io.github.niemannd.meilisearch.json.JacksonJsonProcessor}
     */
    public MeiliClient(Configuration config, HttpClient<?> client, JsonProcessor jsonProcessor) {
        this(config, client, jsonProcessor, new DocumentServiceFactory());
    }

    /**
     *
     * @return the IndexService
     */
    public IndexService indexes() {
        return indexService;
    }

    /**
     *
     * @param documentType a Class that's associated with an Index
     * @return the DocumentService for the Index or 'null' if no Service could be found
     */
    @SuppressWarnings("unchecked")
    public <T> DocumentService<T> documents(Class<T> documentType) {
        return (DocumentService<T>) documentServices.get(documentType);
    }

    /**
     *
     * @param index name of an index
     * @return the DocumentService for the Index
     * @throws MeiliException in case no documentType could be found for the supplied index name
     */
    @SuppressWarnings("unchecked")
    public <T> DocumentService<T> documentServiceForIndex(String index) {
        Optional<Class<?>> documentType = config.getDocumentType(index);
        if (!documentType.isPresent()) {
            throw new MeiliException("documentType could not be found");
        }
        return (DocumentService<T>) documentServices.get(documentType.get());
    }

    /**
     *
     * @return the KeyService
     */
    public KeyService keys() {
        return keyService;
    }

    /**
     *
     * @return the Config for this Client
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     *
     * @return true if the health endpoint returns http status code 200, otherwise false
     */
    public boolean isHealthy() {
        return instanceServices.isHealthy();
    }
    /**
     *
     * @return true if the health status could be set, otherwise false
     */
    public boolean setMaintenance(boolean maintenance) {
        return instanceServices.setMaintenance(maintenance);
    }

    /**
     *
     * @return Version Information of the Meilisearch instance
     */
    public Map<String,String> getVersion() {
        return instanceServices.getVersion();
    }
}
