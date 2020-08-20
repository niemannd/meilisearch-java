package io.github.niemannd.meilisearch.api.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IndexServiceTest {

    private final HttpClient client = mock(HttpClient.class);
    private final JsonProcessor processor = new JacksonJsonProcessor(new ObjectMapper());
    private final IndexService classToTest = new IndexService(client, processor);

    @Test
    void create() {
        when(client.post(any(String.class), any())).thenReturn("{\"uid\":\"movies\",\"primaryKey\":\"id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}");
        Index index = classToTest.createIndex("movies");
        assertEquals("movies", index.getUid());
        assertEquals("id", index.getPrimaryKey());
    }

    @Test
    void createWithPrimaryKey() {
        when(client.post(any(String.class), any())).thenReturn("{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}");
        Index index = classToTest.createIndex("movies", "movie_id");
        assertEquals("movies", index.getUid());
        assertEquals("movie_id", index.getPrimaryKey());
    }

    @Test
    void get() {
        when(client.get(any(String.class), any())).thenReturn("{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}");
        Index index = classToTest.getIndex("movies");
        assertEquals("movies", index.getUid());
        assertEquals("movie_id", index.getPrimaryKey());
    }

    @Test
    void getAll() {
        when(client.get(any(String.class), any())).thenReturn("[{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}]");
        Index[] index = classToTest.getAllIndexes();
        assertEquals(1, index.length);
        assertEquals("movies", index[0].getUid());
        assertEquals("movie_id", index[0].getPrimaryKey());
    }

    @SuppressWarnings("unchecked")
    @Test
    void update() {
        Deque<Map<String, String>> deque = new ArrayDeque<>();
        when(client.put(any(String.class), any(Map.class), any())).then(invocationOnMock -> {
            deque.add((Map<String, String>) invocationOnMock.getArguments()[1]);
            return "{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}";
        });
        Index index = classToTest.updateIndex("movies", "movie_id");
        assertEquals("movies", index.getUid());
        assertEquals("movie_id", index.getPrimaryKey());
    }

    @Test
    void delete() {
        when(client.delete(any(String.class))).thenReturn(true);
        assertTrue(classToTest.deleteIndex("movies"));
    }
}