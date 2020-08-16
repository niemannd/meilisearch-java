package com.github.niemannd.meilisearch.json;

import com.github.niemannd.meilisearch.utils.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GsonJsonProcessorTest {
    private final Gson gson = mock(Gson.class);
    private final GsonJsonProcessor classToTest = new GsonJsonProcessor(gson);

    @Test
    void serialize() {
        when(gson.toJson(any(Movie.class))).thenThrow(new JsonParseException("this is a testexception"));
        assertEquals("test", classToTest.serialize("test"));
        assertNull(classToTest.serialize(new Movie()));
    }

    @Test
    @SuppressWarnings({"RedundantArrayCreation", "ConfusingArgumentToVarargsMethod", "unchecked"})
    void deserialize() {
        when(gson.fromJson(any(String.class), any((Class.class)))).thenThrow(new JsonParseException("this is a testexception"));
        assertNull(classToTest.deserialize("{}", Movie.class));
        assertNull(classToTest.deserialize("{\"id\": 1}", Movie.class, null));
        assertNull(classToTest.deserialize("{}", Movie.class, new Class[0]));
    }

    @Test
    void deserializeString() {
        String content = "{}";
        GsonJsonProcessor gsonJsonProcessor = new GsonJsonProcessor();
        assertEquals(content, gsonJsonProcessor.deserialize(content, String.class));
    }

    @Test
    void deserializeBodyNull() {
        assertNull(classToTest.deserialize(null, List.class, String.class));
    }

    @Test
    @SuppressWarnings({"unchecked", "RedundantArrayCreation", "ConfusingArgumentToVarargsMethod"})
    void deserializeWithParametersEmpty() {
        when(gson.fromJson(any(String.class), any((Class.class)))).thenReturn(new Movie(), new Movie());
        assertNotNull(classToTest.deserialize("{}", Movie.class, null));
        assertNotNull(classToTest.deserialize("{}", Movie.class, new Class[0]));
    }
}