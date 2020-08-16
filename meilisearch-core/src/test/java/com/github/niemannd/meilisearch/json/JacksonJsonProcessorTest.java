package com.github.niemannd.meilisearch.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.niemannd.meilisearch.utils.Movie;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JacksonJsonProcessorTest {
    private final ObjectMapper mapper = mock(ObjectMapper.class);
    private final JacksonJsonProcessor classToTest = new JacksonJsonProcessor(mapper);

    @Test
    void serialize() throws JsonProcessingException {
        when(mapper.writeValueAsString(any())).thenThrow(new JsonMappingException(() -> {
        }, ""));
        assertNull(classToTest.serialize(new Movie()));
    }

    @Test
    @SuppressWarnings({"unchecked", "RedundantArrayCreation", "ConfusingArgumentToVarargsMethod"})
    void deserialize() throws JsonProcessingException {
        when(mapper.readValue(any(String.class), any((Class.class)))).thenAnswer(invocationOnMock -> {
            throw new IOException("");
        });
        assertNull(classToTest.deserialize("{}", Movie.class));
        assertNull(classToTest.deserialize("{}", Movie.class, null));
        assertNull(classToTest.deserialize("{}", Movie.class, new Class[0]));
    }

    @Test
    void deserializeString() {
        String content = "{}";
        assertEquals(content, classToTest.deserialize(content, String.class));
    }

    @Test
    void deserializeBodyNull() {
        assertNull(classToTest.deserialize(null, List.class, String.class));
    }

    @Test
    @SuppressWarnings({"unchecked", "RedundantArrayCreation", "ConfusingArgumentToVarargsMethod"})
    void deserializeWithParametersEmpty() throws JsonProcessingException {
        when(mapper.readValue(any(String.class), any((Class.class)))).thenReturn(new Movie(), new Movie());
        when(mapper.getTypeFactory()).thenAnswer(invocationOnMock -> {
            throw new RuntimeException("don't u dare try to construct a parametric type");
        });
        assertNotNull(classToTest.deserialize("{}", Movie.class, null));
        assertNotNull(classToTest.deserialize("{}", Movie.class, new Class[0]));
    }
}