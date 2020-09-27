package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.config.ConfigurationBuilder;
import io.github.niemannd.meilisearch.http.ApacheHttpClient;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequestFactory;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import io.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MeiliClientBuilderTest {

    private final Configuration config = new ConfigurationBuilder()
            .setKeySupplier(() -> "masterKey")
            .setUrl("http://127.0.0.1:7700")
            .addDocumentType("movies", Movie.class)
            .build();

    private final JsonProcessor processor = new JacksonJsonProcessor();
    private final HttpClient<String> client = mock(ApacheHttpClient.class);
    private final DocumentServiceFactory documentServiceFactory = new DocumentServiceFactory();
    private final GenericServiceTemplate serviceTemplate = new GenericServiceTemplate(client, processor);
    private final BasicHttpRequestFactory requestFactory = new BasicHttpRequestFactory(serviceTemplate);

    @Test
    void basicBuilder() {

        MeiliClient.MeiliClientBuilder builder = MeiliClient.builder()
                .withConfig(config)
                .withDocumentServiceFactory(documentServiceFactory)
                .withHttpClient(client)
                .withServiceTemplate(serviceTemplate)
                .withRequestFactory(requestFactory)
                .withJsonProcessor(processor);

        assertEquals(builder.config, config);
        assertEquals(builder.httpClient, client);
        assertEquals(builder.jsonProcessor, processor);
        assertEquals(builder.documentServiceFactory, documentServiceFactory);
        assertEquals(builder.serviceTemplate, serviceTemplate);
        assertEquals(builder.requestFactory, requestFactory);
    }

    @Test
    void buildTest() {
        MeiliClient.MeiliClientBuilder builder = MeiliClient.builder()
                .withConfig(config)
                .withDocumentServiceFactory(documentServiceFactory)
                .withHttpClient(client)
                .withServiceTemplate(serviceTemplate)
                .withRequestFactory(requestFactory)
                .withJsonProcessor(processor);
        builder.build();

        assertEquals(builder.config, config);
        assertEquals(builder.httpClient, client);
        assertEquals(builder.jsonProcessor, processor);
        assertEquals(builder.documentServiceFactory, documentServiceFactory);
        assertEquals(builder.serviceTemplate, serviceTemplate);
        assertEquals(builder.requestFactory, requestFactory);

        builder = MeiliClient.builder()
                .withConfig(config)
                .withDocumentServiceFactory(documentServiceFactory)
                .withServiceTemplate(serviceTemplate)
                .withRequestFactory(requestFactory);
        builder.build();

        assertEquals(builder.config, config);
        assertNull(builder.httpClient);
        assertNull(builder.jsonProcessor);
        assertEquals(builder.documentServiceFactory, documentServiceFactory);
        assertEquals(builder.serviceTemplate, serviceTemplate);
        assertEquals(builder.requestFactory, requestFactory);

        builder = MeiliClient.builder()
                .withConfig(config)
                .withHttpClient(client)
                .withJsonProcessor(processor);
        builder.build();

        assertEquals(builder.config, config);
        assertEquals(builder.httpClient, client);
        assertEquals(builder.jsonProcessor, processor);
        assertNotNull(builder.documentServiceFactory);
        assertNotNull(builder.serviceTemplate);
        assertNotNull(builder.requestFactory);
    }
}