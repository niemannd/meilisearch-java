package com.github.niemannd.meilisearch.config;

import com.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    private final Configuration classToTest = new ConfigurationBuilder()
            .setUrl("https://localhost:8080")
            .setDocumentTypes(Collections.emptyMap())
            .addDocumentType("movies", Movie.class)
            .setKey(() -> "look at me - i'm a key").build();

    @Test
    void getUrl() {
        assertEquals("https://localhost:8080", classToTest.getUrl());
    }

    @Test
    void getKey() {
        assertEquals("look at me - i'm a key", classToTest.getKey().get());
    }

    @Test
    void getDocumentTypes() {
        assertEquals(1, classToTest.getDocumentTypes().size());
        Assertions.assertThrows(UnsupportedOperationException.class,() -> {
            classToTest.getDocumentTypes().put("test", Movie.class);
        });
    }

    @Test
    void getDocumentType() {
        Optional<Class<?>> movies = classToTest.getDocumentType("movies");
        assertNotNull(movies);
        assertTrue(movies.isPresent());
        assertEquals(Movie.class, movies.get());

        movies = classToTest.getDocumentType("look at me - i'm an index");
        assertNotNull(movies);
        assertFalse(movies.isPresent());
    }

    @Test
    void mustGetDocumentType() {
        assertEquals(Movie.class, classToTest.mustGetDocumentType("movies"));
        Assertions.assertThrows(DocumentTypeNotFoundException.class,() -> {
            classToTest.mustGetDocumentType("look at me - i'm an index");
        });
    }
}