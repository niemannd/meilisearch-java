package io.github.niemannd.meilisearch.http.request;

import io.github.niemannd.meilisearch.http.HttpMethod;

import java.util.Collections;
import java.util.Map;

public class BasicHttpRequest implements HttpRequest, HttpEntity<String> {
    private HttpMethod method;
    private String path;
    private final String content;

    private Map<String, String> headers;

    public BasicHttpRequest(HttpMethod method, String path) {
        this(method, path, null, Collections.emptyMap());

    }

    public BasicHttpRequest(HttpMethod method, String path, String content) {
        this(method, path, content, Collections.emptyMap());
    }

    public BasicHttpRequest(HttpMethod method, String path, String content, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.content = content;
        this.headers = headers;
    }

    public BasicHttpRequest(String method, String path) {
        this(method, path, null, Collections.emptyMap());
    }

    public BasicHttpRequest(String method, String path, String content) {
        this(method, path, content, Collections.emptyMap());
    }

    public BasicHttpRequest(String method, String path, String content, Map<String, String> headers) {
        this(HttpMethod.valueOf(method), path, content, headers);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = HttpMethod.valueOf(method);
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
