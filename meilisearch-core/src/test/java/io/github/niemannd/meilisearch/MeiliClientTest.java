package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliAPIException;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.index.IndexService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.config.ConfigurationBuilder;
import io.github.niemannd.meilisearch.http.ApacheHttpClient;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequestFactory;
import io.github.niemannd.meilisearch.http.response.BasicHttpResponse;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import io.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeiliClientTest {

    private final Configuration config = new ConfigurationBuilder()
            .setKeySupplier(() -> "masterKey")
            .setUrl("http://127.0.0.1:7700")
            .addDocumentType("movies", Movie.class)
            .build();

    private final JsonProcessor processor = new JacksonJsonProcessor();
    private final HttpClient<String> client = mock(ApacheHttpClient.class);
    private MeiliClient classToTest;

    @BeforeEach
    void setUp() {
        GenericServiceTemplate serviceTemplate = new GenericServiceTemplate(client, processor);
        assertDoesNotThrow(() -> classToTest = new MeiliClient(config, serviceTemplate, new DocumentServiceFactory(), new BasicHttpRequestFactory(serviceTemplate)));
    }

    @Test
    void clientCreate() {
        DocumentServiceFactory mock = mock(DocumentServiceFactory.class);
        when(mock.createService(any(), any(), any(), any()))
                .thenThrow(MeiliException.class);

        ServiceTemplate serviceTemplate = new GenericServiceTemplate(client, processor);
        BasicHttpRequestFactory requestFactory = new BasicHttpRequestFactory(serviceTemplate);

        assertThrows(MeiliException.class, () -> new MeiliClient(config, serviceTemplate, mock, requestFactory));
        //noinspection deprecation
        assertDoesNotThrow(() -> new MeiliClient(config, client, processor, new DocumentServiceFactory()));
        //noinspection deprecation
        assertDoesNotThrow(() -> new MeiliClient(config, client, processor));
    }

    @Test
    void getConfig() {
        assertEquals(config, classToTest.getConfig());
    }

    @Test
    void getIndex() {
        assertEquals(IndexService.class, classToTest.indexes().getClass());
    }

    @Test
    void getDocumentService() {
        assertEquals(DocumentService.class, classToTest.documents(Movie.class).getClass());
        assertNull(classToTest.documents(Object.class));
    }

    @Test
    void getDocumentServiceForIndex() {
        DocumentService<Movie> movies = classToTest.documentServiceForIndex("movies");
        assertNotNull(movies);
        assertEquals(classToTest.documents(Movie.class), movies);

        assertThrows(MeiliException.class, () -> classToTest.documentServiceForIndex("notexistingindex"));
    }

    @Test
    void isHealthy() {
        when(client.get(any(), any())).thenReturn(new BasicHttpResponse(null, 200, "")).thenThrow(MeiliAPIException.class);
        assertTrue(classToTest.isHealthy());
        assertFalse(classToTest.isHealthy());
    }

    @Test
    void version() {
        when(client.get(any(), any()))
                .thenReturn(new BasicHttpResponse(null, 200, "{\"commitSha\":\"b46889b5f0f2f8b91438a08a358ba8f05fc09fc1\",\"buildDate\":\"2019-11-15T09:51:54.278247+00:00\",\"pkgVersion\":\"0.1.1\"}"))
                .thenThrow(MeiliException.class);
        when(client.get(any(), any()))
                .thenReturn(new BasicHttpResponse(null, 200, "{\"commitSha\":\"b46889b5f0f2f8b91438a08a358ba8f05fc09fc1\",\"buildDate\":\"2019-11-15T09:51:54.278247+00:00\",\"pkgVersion\":\"0.1.1\"}"))
                .thenThrow(MeiliException.class);

        Map<String, String> version = classToTest.getVersion();
        assertNotNull(version);
        assertTrue(version.containsKey("commitSha"));
        assertEquals("b46889b5f0f2f8b91438a08a358ba8f05fc09fc1", version.get("commitSha"));
        assertTrue(version.containsKey("buildDate"));
        assertEquals("2019-11-15T09:51:54.278247+00:00", version.get("buildDate"));
        assertTrue(version.containsKey("pkgVersion"));
        assertEquals("0.1.1", version.get("pkgVersion"));
        version = classToTest.getVersion();
        assertNotNull(version);
        assertEquals(0, version.size());
    }

    @Test
    void maintenance() {
        when(client.put(any(), any(), any())).thenReturn(new BasicHttpResponse(null, 200, "")).thenThrow(MeiliException.class);
        assertTrue(classToTest.setMaintenance(true));
        assertFalse(classToTest.setMaintenance(true));
    }

}