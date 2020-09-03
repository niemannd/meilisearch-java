package io.github.niemannd.meilisearch.api.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.niemannd.meilisearch.api.documents.Update;
import io.github.niemannd.meilisearch.http.ApacheHttpClient;
import io.github.niemannd.meilisearch.http.BasicHttpResponse;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IndexServiceTest {

    private final HttpClient<String> client = mock(ApacheHttpClient.class);
    private final JsonProcessor processor = new JacksonJsonProcessor(new ObjectMapper());
    private final SettingsService settingsService = mock(SettingsService.class);
    private final IndexService classToTest = new IndexService(client, processor, settingsService);

    @Test
    void create() {
        when(client.post(any(String.class), any())).thenReturn(new BasicHttpResponse(null, 200, "{\"uid\":\"movies\",\"primaryKey\":\"id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}"));
        Index index = classToTest.createIndex("movies");
        assertEquals("movies", index.getUid());
        assertEquals("id", index.getPrimaryKey());
    }

    @Test
    void createWithPrimaryKey() {
        when(client.post(any(String.class), any())).thenReturn(new BasicHttpResponse(null, 200, "{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}"));
        Index index = classToTest.createIndex("movies", "movie_id");
        assertEquals("movies", index.getUid());
        assertEquals("movie_id", index.getPrimaryKey());
    }

    @Test
    void get() {
        when(client.get(any(String.class), any())).thenReturn(new BasicHttpResponse(null, 200, "{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}"));
        Index index = classToTest.getIndex("movies");
        assertEquals("movies", index.getUid());
        assertEquals("movie_id", index.getPrimaryKey());
    }

    @Test
    void getAll() {
        when(client.get(any(String.class), any())).thenReturn(new BasicHttpResponse(null, 200, "[{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}]"));
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
            return new BasicHttpResponse(null, 200, "{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711476Z\",\"updatedAt\":\"2019-11-20T09:40:33.711476Z\"}");
        });
        Index index = classToTest.updateIndex("movies", "movie_id");
        assertEquals("movies", index.getUid());
        assertEquals("movie_id", index.getPrimaryKey());
    }

    @Test
    void delete() {
        when(client.delete(any(String.class))).thenReturn(new BasicHttpResponse(null, 204, ""));
        assertTrue(classToTest.deleteIndex("movies"));
        when(client.delete(any(String.class))).thenReturn(new BasicHttpResponse(null, 404, ""));
        assertFalse(classToTest.deleteIndex("movies"));
        when(client.delete(any(String.class))).thenReturn(new BasicHttpResponse(null, 100, ""));
        assertFalse(classToTest.deleteIndex("movies"));
    }

    @Test
    void settings() {
        Settings dummySettings = new Settings().setDistinctAttribute("test");
        Update dummyUpdate = new Update();
        when(settingsService.getSettings(any())).thenReturn(dummySettings);
        when(settingsService.resetSettings(any())).thenReturn(dummyUpdate);
        when(settingsService.updateSettings(any(), any())).thenReturn(dummyUpdate);

        assertThat(classToTest.getSettings("test"), is(equalTo(dummySettings)));
        assertThat(classToTest.resetSettings("test"), is(equalTo(dummyUpdate)));
        assertThat(classToTest.updateSettings("test",dummySettings), is(equalTo(dummyUpdate)));
    }
}