package io.github.niemannd.meilisearch.http;

import io.github.niemannd.meilisearch.api.MeiliAPIException;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.documents.DocumentService;
import io.github.niemannd.meilisearch.api.index.Index;
import io.github.niemannd.meilisearch.api.index.IndexService;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.config.ConfigurationBuilder;
import io.github.niemannd.meilisearch.http.response.HttpResponse;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.utils.Movie;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.MinimalHttpClient;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApacheHttpClientTest {
    private final JacksonJsonProcessor processor = new JacksonJsonProcessor();
    private Supplier<String> keySupplier = () -> "masterKey";
    private final Configuration config = new ConfigurationBuilder().setUrl("http://lavaridge:7700").setKey(keySupplier).build();

    private final MinimalHttpClient client = mock(MinimalHttpClient.class);
    private final ApacheHttpClient classToTest = new ApacheHttpClient(client, config, processor);
    private final IndexService service = new IndexService(classToTest, processor);

    private final ArrayDeque<ClassicHttpRequest> requests = new ArrayDeque<>();
    private final ArrayDeque<ClassicHttpResponse> responses = new ArrayDeque<>();

    @BeforeEach
    void setUp() throws IOException {
        requests.clear();
        responses.clear();
        when(client.execute(any())).then(invocationOnMock -> {
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
            return (CloseableHttpResponse) adaptMethod.invoke(null, request);
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
    void getWithMeiliError() {
        responses.add(this.getResponse(404, "{\"message\":\"Document with id 1 not found\",\"errorCode\":\"document_not_found\",\"errorType\":\"invalid_request_error\",\"errorLink\":\"https://docs.meilisearch.com/errors#document_not_found\"}"));
        DocumentService<Movie> movies = new DocumentService<>("movies", classToTest, config, processor);
        MeiliAPIException exception = assertThrows(MeiliAPIException.class, () -> movies.getDocument("1"));
        assertTrue(exception.hasError());
        assertEquals("document_not_found", exception.getError().getErrorCode());
        assertEquals("Document with id 1 not found", exception.getError().getMessage());
        assertEquals("invalid_request_error", exception.getError().getErrorType());
        assertEquals("https://docs.meilisearch.com/errors#document_not_found", exception.getError().getErrorLink());
    }


    @Test
    void requestsWithException() throws IOException {
        String message = "oh boy!";
        CloseableHttpResponse mock = mock(CloseableHttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        when(entity.getContent()).thenAnswer(invocationOnMock -> new ByteArrayInputStream(message.getBytes()));
        when(mock.getCode()).thenReturn(200);
        when(mock.getEntity()).thenReturn(entity);

        when(mock.getHeaders()).thenReturn(Stream.of(new BasicHeader("name1", "test"), new BasicHeader("name2", "test")).toArray(Header[]::new));

        doThrow(new IOException(message)).when(mock).close();
        reset(client);
        when(client.execute(any())).then(invocationOnMock -> {
            requests.add(invocationOnMock.getArgument(0));
            return responses.poll();
        });
        responses.addAll(Arrays.asList(mock, mock, mock));

        assertThrows(MeiliAPIException.class, () -> this.service.createIndex("0"));
        assertThrows(MeiliAPIException.class, () -> this.service.getIndex("0"));
        assertThrows(MeiliAPIException.class, () -> this.service.updateIndex("0", "0"));
    }

    @Test
    void requestWithoutEntity() throws IOException {
        CloseableHttpResponse mock = mock(CloseableHttpResponse.class);
        when(mock.getCode()).thenReturn(404);
        when(mock.getEntity()).thenReturn(null);
        when(mock.getHeaders()).thenReturn(new Header[0]);
        responses.addAll(Arrays.asList(mock, mock, mock, mock));

        reset(client);
        when(client.execute(any())).thenAnswer(invocationOnMock -> {
            requests.add(invocationOnMock.getArgument(0));
            return responses.poll();
        });

        MeiliAPIException exception = assertThrows(MeiliAPIException.class, () -> this.service.createIndex("0"));
        assertEquals("empty response without success code", exception.getMessage());
        exception = assertThrows(MeiliAPIException.class, () -> this.service.getIndex("0"));
        assertEquals("empty response without success code", exception.getMessage());
        exception = assertThrows(MeiliAPIException.class, () -> this.service.updateIndex("0", "0"));
        assertEquals("empty response without success code", exception.getMessage());
        assertThrows(MeiliAPIException.class, () -> this.service.deleteIndex("0"));
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
        assertThrows(MeiliException.class, () -> service.deleteIndex("movies"));
    }

    @Test
    void delete100() {
        responses.add(this.getResponse(100, null));
        assertThrows(MeiliAPIException.class, () -> service.deleteIndex("movies"));
        ClassicHttpRequest request = requests.poll();
        assertNotNull(request);
        assertEquals("/indexes/movies", request.getRequestUri());
        assertEquals(HttpDelete.class, request.getClass());
        assertEquals("masterKey", request.getFirstHeader("X-Meili-API-Key").getValue());
        assertThrows(MeiliAPIException.class, () -> service.deleteIndex("movies"));
    }

    @Test
    void delete404() {
        responses.add(this.getResponse(404, null));
        assertThrows(MeiliAPIException.class, () -> service.deleteIndex("movies"));
        ClassicHttpRequest request = requests.poll();
        assertNotNull(request);
        assertEquals("/indexes/movies", request.getRequestUri());
        assertEquals(HttpDelete.class, request.getClass());
        assertEquals("masterKey", request.getFirstHeader("X-Meili-API-Key").getValue());
        assertThrows(MeiliAPIException.class, () -> service.deleteIndex("movies"));
    }

    @Test
    void queryStringGeneration() {
        HashMap<String, String> params = new HashMap<>();
        params.put("q", "Test Test Test");
        params.put("list", "blu,da,be,dee,da,be,dei");
        params.put("asterisks", "*");
        assertEquals("q=Test Test Test&asterisks=*&list=blu,da,be,dee,da,be,dei", classToTest.createQueryString(params));
    }

    @Test
    void keySupplier() throws IOException {
        reset(client);
        when(client.execute(any())).thenAnswer(invocationOnMock -> {
            requests.add(invocationOnMock.getArgument(0));
            return responses.poll();
        });
        responses.add(this.getResponse(200, "{}"));
        BasicClassicHttpRequest dummyRequest = new BasicClassicHttpRequest("GET", "/");
        HttpResponse<String> execute = classToTest.execute(dummyRequest);
        assertThat(execute, notNullValue());
        assertThat(execute.getStatusCode(), is(200));
        assertThat(execute.getContent(), is("{}"));

        ClassicHttpRequest request = requests.poll();
        assertThat(request, notNullValue());
        assertThat(request.getFirstHeader("X-Meili-API-Key").getValue(), is("masterKey"));
    }

    @Test
    void keySupplierNull() {
        BasicClassicHttpRequest dummyRequest = new BasicClassicHttpRequest("GET", "/");
        Configuration config = new ConfigurationBuilder().setUrl("http://lavaridge:7700").setKey(null).build();
        ApacheHttpClient classForTest = new ApacheHttpClient(client, config, processor);

        responses.add(this.getResponse(200, "{}"));
        classForTest.execute(dummyRequest);
        ClassicHttpRequest request = requests.poll();
        assertThat(request, notNullValue());
        assertThat(request.getFirstHeader("X-Meili-API-Key"), nullValue());
    }

    @Test
    void keySupplierGetNull() {
        BasicClassicHttpRequest dummyRequest = new BasicClassicHttpRequest("GET", "/");
        Configuration config = new ConfigurationBuilder().setUrl("http://lavaridge:7700").setKey(() -> null).build();
        ApacheHttpClient classForTest = new ApacheHttpClient(client, config, processor);

        responses.add(this.getResponse(200, "{}"));
        classForTest.execute(dummyRequest);
        ClassicHttpRequest request = requests.poll();
        assertThat(request, notNullValue());
        assertThat(request.getFirstHeader("X-Meili-API-Key"), nullValue());
    }

    @Test
    void defaultKeySupplier() {
        BasicClassicHttpRequest dummyRequest = new BasicClassicHttpRequest("GET", "/");
        Configuration config = new ConfigurationBuilder().setUrl("http://lavaridge:7700").build();
        ApacheHttpClient classForTest = new ApacheHttpClient(client, config, processor);

        responses.add(this.getResponse(200, "{}"));
        classForTest.execute(dummyRequest);
        ClassicHttpRequest request = requests.poll();
        assertThat(request, notNullValue());
        assertThat(request.getFirstHeader("X-Meili-API-Key"), nullValue());
    }
}