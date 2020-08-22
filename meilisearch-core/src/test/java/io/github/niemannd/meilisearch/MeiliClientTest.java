package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.index.IndexService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.config.ConfigurationBuilder;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import io.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeiliClientTest {

    private final Configuration config = new ConfigurationBuilder()
            .setKey(() -> "masterKey")
            .setUrl("http://127.0.0.1:7700")
            .addDocumentType("movies", Movie.class)
            .build();

    private final JsonProcessor processor = mock(JsonProcessor.class);
    private final HttpClient client = mock(HttpClient.class);
    private MeiliClient classToTest;

    @BeforeEach
    void setUp() {
        assertDoesNotThrow(() -> classToTest = new MeiliClient(config, client, processor, new DocumentServiceFactory()));
    }

    @Test
    void clientCreate() {
        DocumentServiceFactory mock = mock(DocumentServiceFactory.class);
        when(mock.createService(any(), any(), any(), any(), any()))
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
}