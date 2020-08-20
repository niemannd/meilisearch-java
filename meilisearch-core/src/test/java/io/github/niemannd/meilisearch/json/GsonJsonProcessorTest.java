package io.github.niemannd.meilisearch.json;

import io.github.niemannd.meilisearch.api.MeiliJSONException;
import io.github.niemannd.meilisearch.utils.Movie;
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
        assertThrows(MeiliJSONException.class, () -> classToTest.serialize(new Movie()));
    }

    @Test
    @SuppressWarnings({"ConfusingArgumentToVarargsMethod", "unchecked"})
    void deserialize() {
        when(gson.fromJson(any(String.class), any((Class.class)))).thenThrow(new JsonParseException("this is a testexception"));
        assertThrows(MeiliJSONException.class, () -> classToTest.deserialize("{}", Movie.class));
        assertThrows(MeiliJSONException.class, () -> classToTest.deserialize("{\"id\": 1}", Movie.class, null));
        assertThrows(MeiliJSONException.class, () -> classToTest.deserialize("{}", Movie.class, new Class[0]));
    }

    @Test
    void deserializeString() {
        String content = "{}";
        GsonJsonProcessor gsonJsonProcessor = new GsonJsonProcessor();
        assertEquals(content, gsonJsonProcessor.deserialize(content, String.class));
    }

    @Test
    void deserializeBodyNull() {
        assertThrows(MeiliJSONException.class, () -> classToTest.deserialize(null, List.class, String.class));
    }

    @Test
    @SuppressWarnings({"unchecked", "RedundantArrayCreation", "ConfusingArgumentToVarargsMethod"})
    void deserializeWithParametersEmpty() {
        when(gson.fromJson(any(String.class), any((Class.class)))).thenReturn(new Movie(), new Movie());
        assertNotNull(classToTest.deserialize("{}", Movie.class, null));
        assertNotNull(classToTest.deserialize("{}", Movie.class, new Class[0]));
    }
}