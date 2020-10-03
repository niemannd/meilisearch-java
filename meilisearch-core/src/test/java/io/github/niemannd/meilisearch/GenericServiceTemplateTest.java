package io.github.niemannd.meilisearch;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.HttpMethod;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequest;
import io.github.niemannd.meilisearch.http.request.HttpRequest;
import io.github.niemannd.meilisearch.http.response.HttpResponse;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericServiceTemplateTest {

    private final HttpClient<?> client = mock(HttpClient.class);
    private final JsonProcessor processor = mock(JsonProcessor.class);
    private final GenericServiceTemplate classToTest = new GenericServiceTemplate(client, processor);

    @Test
    void getClient() {
        assertEquals(client, classToTest.getClient());
    }

    @Test
    void getProcessor() {
        assertEquals(processor, classToTest.getProcessor());
    }

    @Test
    void executeGet() {
        when(client.get(any(String.class), any())).thenAnswer(invocationOnMock -> new MockHttpResponse(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1), null));
        BasicHttpRequest request = new BasicHttpRequest(HttpMethod.GET, "/path");
        MockHttpResponse response = classToTest.execute(request, null);
        assertEquals(request.getPath(), response.getRequestPath());
        assertEquals(request.getHeaders(), response.getHeaders());
    }

    @Test
    void executePost() {
        when(client.post(any(String.class), any())).thenAnswer(invocationOnMock -> new MockHttpResponse(invocationOnMock.getArgument(0), Collections.emptyMap(), invocationOnMock.getArgument(1))).thenAnswer(invocationOnMock -> new MockHttpResponse(invocationOnMock.getArgument(0), Collections.emptyMap(), invocationOnMock.getArgument(1)));
        BasicHttpRequest fullRequest = new BasicHttpRequest(HttpMethod.POST, "/path", "content");
        MockHttpResponse response = classToTest.execute(fullRequest, null);
        assertEquals(fullRequest.getPath(), response.getRequestPath());
        assertEquals(fullRequest.getContent(), response.getContent());
        assertEquals(fullRequest.getHeaders(), response.getHeaders());

        MockHttpRequest contentlessRequest = new MockHttpRequest(HttpMethod.POST, "/path", Collections.emptyMap());
        response = classToTest.execute(contentlessRequest, null);
        assertEquals(contentlessRequest.getPath(), response.getRequestPath());
        assertNull(response.getContent());
        assertEquals(contentlessRequest.getHeaders(), response.getHeaders());
    }

    @Test
    void executePut() {
        when(client.put(any(String.class), any(), any())).thenAnswer(invocationOnMock -> new MockHttpResponse(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1), invocationOnMock.getArgument(2))).thenAnswer(invocationOnMock -> new MockHttpResponse(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1), invocationOnMock.getArgument(2)));
        BasicHttpRequest fullRequest = new BasicHttpRequest(HttpMethod.PUT, "/path", "content");
        MockHttpResponse response = classToTest.execute(fullRequest, null);
        assertEquals(fullRequest.getPath(), response.getRequestPath());
        assertEquals(fullRequest.getContent(), response.getContent());
        assertEquals(fullRequest.getHeaders(), response.getHeaders());

        MockHttpRequest contentlessRequest = new MockHttpRequest(HttpMethod.PUT, "/path", Collections.emptyMap());
        response = classToTest.execute(contentlessRequest, null);
        assertEquals(contentlessRequest.getPath(), response.getRequestPath());
        assertNull(response.getContent());
        assertEquals(contentlessRequest.getHeaders(), response.getHeaders());
    }

    @Test
    void executeDelete() {
        when(client.delete(any(String.class))).thenAnswer(invocationOnMock -> new MockHttpResponse(invocationOnMock.getArgument(0), Collections.emptyMap(), null)).thenAnswer(invocationOnMock -> new MockHttpResponse(invocationOnMock.getArgument(0), Collections.emptyMap(), null));
        BasicHttpRequest fullRequest = new BasicHttpRequest(HttpMethod.DELETE, "/path", "content");
        MockHttpResponse response = classToTest.execute(fullRequest, null);
        assertEquals(fullRequest.getPath(), response.getRequestPath());
        assertNull(response.getContent());
        assertEquals(fullRequest.getHeaders(), response.getHeaders());

        MockHttpRequest contentlessRequest = new MockHttpRequest(HttpMethod.DELETE, "/path", Collections.emptyMap());
        response = classToTest.execute(contentlessRequest, null);
        assertEquals(contentlessRequest.getPath(), response.getRequestPath());
        assertNull(response.getContent());
        assertEquals(contentlessRequest.getHeaders(), response.getHeaders());
    }

    @Test
    void defaultBranch() {
        assertThrows(MeiliException.class, () -> classToTest.execute(new MockHttpRequest(HttpMethod.OPTIONS, null, null), null));
    }

    public static class MockHttpRequest implements HttpRequest {
        private HttpMethod method;
        private String path;

        private Map<String, String> headers;

        public MockHttpRequest(HttpMethod method, String path, Map<String, String> headers) {
            this.method = method;
            this.path = path;
            this.headers = headers;
        }

        @Override
        public HttpMethod getMethod() {
            return method;
        }

        @Override
        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public Map<String, String> getHeaders() {
            return headers;
        }

        @Override
        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }
    }

    public static class MockHttpResponse implements HttpResponse<String> {

        private final String requestPath;
        private final Map<String, String> requestHeaders;
        private final String requestBody;

        public MockHttpResponse(String requestPath, Map<String, String> requestHeaders, String requestBody) {
            this.requestPath = requestPath;
            this.requestHeaders = requestHeaders;
            this.requestBody = requestBody;
        }

        @Override
        public Map<String, String> getHeaders() {
            return requestHeaders;
        }

        @Override
        public int getStatusCode() {
            return 0;
        }

        @Override
        public String getContent() {
            return requestBody;
        }

        public String getRequestPath() {
            return requestPath;
        }
    }
}