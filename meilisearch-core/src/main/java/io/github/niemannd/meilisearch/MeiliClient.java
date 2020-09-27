package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.index.IndexService;
import io.github.niemannd.meilisearch.api.instance.InstanceServices;
import io.github.niemannd.meilisearch.api.keys.KeyService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequestFactory;
import io.github.niemannd.meilisearch.http.request.HttpRequestFactory;
import io.github.niemannd.meilisearch.json.JsonProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Basic Meilisearch Client
 */
public class MeiliClient {
    private final Configuration config;

    private final IndexService indexService;
    private final KeyService keyService;
    private final InstanceServices instanceServices;

    private final HashMap<Class<?>, DocumentService<?>> documentServices = new HashMap<>();

    /**
     * Creates a MeilisearchClient for the given {@code config}, {@code serviceTemplate} and {@code documentServiceFactory}.
     *
     * @param config                 Configuration created by using a {@link io.github.niemannd.meilisearch.config.ConfigurationBuilder}
     * @param serviceTemplate        ServiceTempalte that defines how {@link HttpClient} and {@link JsonProcessor} work together
     * @param documentServiceFactory DocumentServiceFactory for ServiceCreation
     */
    public MeiliClient(Configuration config, ServiceTemplate serviceTemplate, DocumentServiceFactory documentServiceFactory, HttpRequestFactory requestFactory) {
        this.config = config;
        HttpRequestFactory factory = requestFactory == null ? new BasicHttpRequestFactory(serviceTemplate) : requestFactory;
        this.indexService = new IndexService(serviceTemplate, factory);
        this.keyService = new KeyService(serviceTemplate, factory);
        this.instanceServices = new InstanceServices(serviceTemplate, factory);

        Map<String, Class<?>> documentTypes = config.getDocumentTypes();
        for (Map.Entry<String, Class<?>> index : documentTypes.entrySet()) {
            documentServices.put(
                    documentTypes.get(index.getKey()),
                    documentServiceFactory.createService(index.getKey(), config, serviceTemplate,factory)
            );
        }
    }

    /**
     * Creates a MeilisearchClient for the given {@code config}, {@code client},{@code jsonProcessor} and {@code documentServiceFactory}.
     * This Constructor will use a {@link GenericServiceTemplate} internally
     *
     * @deprecated
     *
     * @param config                 Configuration created by using a {@link io.github.niemannd.meilisearch.config.ConfigurationBuilder}
     * @param client                 an HTTPClient, e.g. {@link io.github.niemannd.meilisearch.http.ApacheHttpClient}
     * @param jsonProcessor          an JsonProcessor , e.g. {@link io.github.niemannd.meilisearch.json.JacksonJsonProcessor}
     * @param documentServiceFactory DocumentServiceFactory for ServiceCreation
     */
    @Deprecated
    public MeiliClient(Configuration config, HttpClient<?> client, JsonProcessor jsonProcessor, DocumentServiceFactory documentServiceFactory) {
        this(config, new GenericServiceTemplate(client, jsonProcessor), documentServiceFactory, null);
    }

    /**
     * Creates a MeilisearchClient for the given {@code config}, {@code client},{@code jsonProcessor}.
     * Uses the default {@link DocumentServiceFactory} for ServiceCreation
     *
     * @deprecated
     *
     * @param config        Configuration created by using a {@link io.github.niemannd.meilisearch.config.ConfigurationBuilder}
     * @param client        an HTTPClient, e.g. {@link io.github.niemannd.meilisearch.http.ApacheHttpClient}
     * @param jsonProcessor an JsonProcessor , e.g. {@link io.github.niemannd.meilisearch.json.JacksonJsonProcessor}
     */
    @Deprecated
    public MeiliClient(Configuration config, HttpClient<?> client, JsonProcessor jsonProcessor) {
        this(config, new GenericServiceTemplate(client, jsonProcessor), new DocumentServiceFactory(), null);
    }

    /**
     * @return the IndexService
     */
    public IndexService indexes() {
        return indexService;
    }

    /**
     * @param documentType a Class that's associated with an Index
     * @return the DocumentService for the Index or 'null' if no Service could be found
     */
    @SuppressWarnings("unchecked")
    public <T> DocumentService<T> documents(Class<T> documentType) {
        return (DocumentService<T>) documentServices.get(documentType);
    }

    /**
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
     * @return the KeyService
     */
    public KeyService keys() {
        return keyService;
    }

    /**
     * @return the Config for this Client
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * @return true if the health endpoint returns http status code 200, otherwise false
     */
    public boolean isHealthy() {
        return instanceServices.isHealthy();
    }

    /**
     * @return true if the health status could be set, otherwise false
     */
    public boolean setMaintenance(boolean maintenance) {
        return instanceServices.setMaintenance(maintenance);
    }

    /**
     * @return Version Information of the Meilisearch instance
     */
    public Map<String, String> getVersion() {
        return instanceServices.getVersion();
    }


    public static MeiliClientBuilder builder() {
        return new MeiliClientBuilder();
    }

    public static class MeiliClientBuilder {
        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MeiliClientBuilder.class);

        Configuration config;
        HttpClient<?> httpClient;
        JsonProcessor jsonProcessor;
        ServiceTemplate serviceTemplate;
        HttpRequestFactory requestFactory;
        DocumentServiceFactory documentServiceFactory;

        public MeiliClientBuilder withConfig(Configuration config) {
            this.config = config;
            return this;
        }

        public MeiliClientBuilder withHttpClient(HttpClient<?> httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public MeiliClientBuilder withJsonProcessor(JsonProcessor jsonProcessor) {
            this.jsonProcessor = jsonProcessor;
            return this;
        }

        public MeiliClientBuilder withServiceTemplate(ServiceTemplate serviceTemplate) {
            this.serviceTemplate = serviceTemplate;
            return this;
        }

        public MeiliClientBuilder withRequestFactory(HttpRequestFactory requestFactory) {
            this.requestFactory = requestFactory;
            return this;
        }

        public MeiliClientBuilder withDocumentServiceFactory(DocumentServiceFactory documentServiceFactory) {
            this.documentServiceFactory = documentServiceFactory;
            return this;
        }

        public MeiliClient build() {
            if(httpClient != null && serviceTemplate != null) {
                log.warn("Both httpClient and serviceTemplate are set - httpClient will be ignored");
            }
            if(jsonProcessor != null && serviceTemplate != null) {
                log.warn("Both jsonProcessor and serviceTemplate are set - jsonProcessor will be ignored");
            }
            if (serviceTemplate == null) {
                serviceTemplate = new GenericServiceTemplate(httpClient, jsonProcessor);
            }
            if (documentServiceFactory == null) {
                documentServiceFactory = new DocumentServiceFactory();
            }
            if (requestFactory == null) {
                requestFactory = new BasicHttpRequestFactory(serviceTemplate);
            }
            return new MeiliClient(config, serviceTemplate, documentServiceFactory, requestFactory);
        }
    }
}
