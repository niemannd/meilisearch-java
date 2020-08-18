package com.github.niemannd.meilisearch.http;

import com.github.niemannd.meilisearch.api.MeiliException;
import com.github.niemannd.meilisearch.api.index.Index;
import com.github.niemannd.meilisearch.api.index.IndexService;
import com.github.niemannd.meilisearch.config.Configuration;
import com.github.niemannd.meilisearch.config.ConfigurationBuilder;
import com.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.MinimalHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApacheHttpClientTest {
    private final JacksonJsonProcessor processor = new JacksonJsonProcessor();
    private final Configuration config = new ConfigurationBuilder().setUrl("http://lavaridge:7700").setKey(() -> "masterKey").build();
    private final MinimalHttpClient client = mock(MinimalHttpClient.class);
    private final ApacheHttpClient classToTest = new ApacheHttpClient(client, config, processor);
    private final IndexService service = new IndexService(classToTest, processor);

    private ArrayDeque<ClassicHttpRequest> requests = new ArrayDeque<>();
    private ArrayDeque<ClassicHttpResponse> responses = new ArrayDeque<>();

    @BeforeEach
    void setUp() throws IOException {
        requests.clear();
        responses.clear();
        when(client.execute(any(ClassicHttpRequest.class))).then(invocationOnMock -> {
            requests.add(invocationOnMock.getArgument(0));
            return responses.poll();
        }).then(invocationOnMock -> {
            throw new IOException("oh boy!");
        });
    }

    private CloseableHttpResponse getResponse(int status, String content) {
        BasicClassicHttpResponse request = new BasicClassicHttpResponse(status);
        if (content != null)
            request.setEntity(new BasicHttpEntity(new ByteArrayInputStream(content.getBytes()), content.getBytes().length, ContentType.APPLICATION_JSON, "UTF-8"));
        try {
            Method adaptMethod = Class.forName("org.apache.hc.client5.http.impl.classic.CloseableHttpResponse")
                    .getDeclaredMethod("adapt", ClassicHttpResponse.class);
            adaptMethod.setAccessible(true);
            return (CloseableHttpResponse) adaptMethod
                    .invoke(null, request);
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void constructor() {
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient(config, processor);
    }

    @Test
    void get() {
        responses.add(this.getResponse(200, "[{\"uid\":\"movies\",\"primaryKey\":\"movie_id\",\"createdAt\":\"2019-11-20T09:40:33.711324Z\",\"updatedAt\":\"2019-11-20T10:16:42.761858Z\"}]"));
        Index[] all = service.getAllIndexes();
        assertEquals(1, all.length);
        assertEquals("movies", all[0].getUid());
        assertEquals("movie_id", all[0].getPrimaryKey());
        ClassicHttpRequest request = requests.poll();
        assertNotNull(request);
        assertEquals("/indexes?", request.getRequestUri());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("masterKey", request.getFirstHeader("X-Meili-API-Key").getValue());
        assertThrows(MeiliException.class, service::getAllIndexes);
    }

    @Test
    void post() {
        responses.add(this.getResponse(200, "{\"uid\":\"movies\",\"primaryKey\":\"movie_id\"}"));
        Index index = service.createIndex("uid", "primaryKey");
        assertNotNull(index);
        assertEquals("movies", index.getUid());
        assertEquals("movie_id", index.getPrimaryKey());
        ClassicHttpRequest request = requests.poll();
        assertNotNull(request);
        assertEquals("/indexes", request.getRequestUri());
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("masterKey", request.getFirstHeader("X-Meili-API-Key").getValue());
        assertThrows(MeiliException.class, () -> service.createIndex("uid", "primaryKey"));

    }

    @Test
    void put() {
        responses.add(this.getResponse(200, "{\"uid\":\"movies\",\"primaryKey\":\"movie_id\"}"));
        Index index = service.updateIndex("movies", "movie_id");
        assertNotNull(index);
        assertEquals("movies", index.getUid());
        assertEquals("movie_id", index.getPrimaryKey());
        ClassicHttpRequest request = requests.poll();
        assertNotNull(request);
        assertEquals("/indexes/movies", request.getRequestUri());
        assertEquals(HttpPut.class, request.getClass());
        assertEquals("masterKey", request.getFirstHeader("X-Meili-API-Key").getValue());
        assertThrows(MeiliException.class, () -> service.updateIndex("movies", "movie_id"));
    }

    @Test
    void putEmptyBody() {
        responses.add(this.getResponse(200, "{\"uid\":\"movies\",\"primaryKey\":\"movie_id\"}"));
        classToTest.put("/dummy", Collections.emptyMap(), null);
        ClassicHttpRequest req = requests.poll();
        assertNotNull(req);
        assertEquals("masterKey", req.getFirstHeader("X-Meili-API-Key").getValue());
        assertNull(req.getEntity());
    }

    @Test
    void delete() {
        responses.add(this.getResponse(204, null));
        assertTrue(service.deleteIndex("movies"));
        ClassicHttpRequest request = requests.poll();
        assertNotNull(request);
        assertEquals("/indexes/movies", request.getRequestUri());
        assertEquals(HttpDelete.class, request.getClass());
        assertEquals("masterKey", request.getFirstHeader("X-Meili-API-Key").getValue());
        assertFalse(service.deleteIndex("movies"));
    }

    @Test
    void delete100() {
        responses.add(this.getResponse(100, null));
        assertFalse(service.deleteIndex("movies"));
        ClassicHttpRequest request = requests.poll();
        assertNotNull(request);
        assertEquals("/indexes/movies", request.getRequestUri());
        assertEquals(HttpDelete.class, request.getClass());
        assertEquals("masterKey", request.getFirstHeader("X-Meili-API-Key").getValue());
        assertFalse(service.deleteIndex("movies"));
    }

    @Test
    void delete404() {
        responses.add(this.getResponse(404, null));
        assertFalse(service.deleteIndex("movies"));
        ClassicHttpRequest request = requests.poll();
        assertNotNull(request);
        assertEquals("/indexes/movies", request.getRequestUri());
        assertEquals(HttpDelete.class, request.getClass());
        assertEquals("masterKey", request.getFirstHeader("X-Meili-API-Key").getValue());
        assertFalse(service.deleteIndex("movies"));
    }

    @Test
    void queryStringgeneration() {
        HashMap<String, String> params = new HashMap<>();
        params.put("q", "Test Test Test");
        params.put("list", "blu,da,be,dee,da,be,dei");
        params.put("asteriks", "*");
        assertEquals("q=Test Test Test&asteriks=*&list=blu,da,be,dee,da,be,dei", classToTest.createQueryString(params));
    }
}