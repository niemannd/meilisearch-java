package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliAPIException;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.index.IndexService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.config.ConfigurationBuilder;
import io.github.niemannd.meilisearch.http.HttpClient;
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
            .setKey(() -> "masterKey")
            .setUrl("http://127.0.0.1:7700")
            .addDocumentType("movies", Movie.class)
            .build();

    private final JsonProcessor processor = new JacksonJsonProcessor();
    private final HttpClient client = mock(HttpClient.class);
    private MeiliClient classToTest;

    @BeforeEach
    void setUp() {
        assertDoesNotThrow(() -> classToTest = new MeiliClient(config, client, processor, new DocumentServiceFactory()));
    }

    @Test
    void clientCreate() {
        DocumentServiceFactory mock = mock(DocumentServiceFactory.class);
        when(mock.createService(any(), any(), any(), any()))
                .thenThrow(MeiliException.class);

        assertThrows(MeiliException.class, () -> new MeiliClient(config, client, processor, mock));
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
        when(client.get(any(), any())).thenReturn("").thenThrow(MeiliAPIException.class);
        assertTrue(classToTest.isHealthy());
        assertFalse(classToTest.isHealthy());
    }

    @Test
    void version() {
        when(client.get(any(), any()))
                .thenReturn("{\"commitSha\":\"b46889b5f0f2f8b91438a08a358ba8f05fc09fc1\",\"buildDate\":\"2019-11-15T09:51:54.278247+00:00\",\"pkgVersion\":\"0.1.1\"}")
                .thenThrow(MeiliException.class);
        when(client.get(any(), any()))
                .thenReturn("{\"commitSha\":\"b46889b5f0f2f8b91438a08a358ba8f05fc09fc1\",\"buildDate\":\"2019-11-15T09:51:54.278247+00:00\",\"pkgVersion\":\"0.1.1\"}")
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
        when(client.put(any(), any(),any())).thenReturn("").thenThrow(MeiliException.class);
        assertTrue(classToTest.setMaintenance(true));
        assertFalse(classToTest.setMaintenance(true));
    }

}