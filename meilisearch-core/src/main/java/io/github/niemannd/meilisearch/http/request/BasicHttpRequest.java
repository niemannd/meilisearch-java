package io.github.niemannd.meilisearch.http.request;

import java.util.Collections;
import java.util.Map;

public class BasicHttpRequest implements HttpRequest, HttpEntity<String> {
    private String method;
    private String path;
    private final String content;

    private Map<String, String> headers;

    public BasicHttpRequest(String method, String path) {
        this(method,path,null,Collections.emptyMap());
    }

    public BasicHttpRequest(String method, String path, String content) {
        this(method,path,content,Collections.emptyMap());
    }

    public BasicHttpRequest(String method, String path, String content, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.content = content;
        this.headers = headers;
    }

    @Override
    public String getContent() throws UnsupportedOperationException {
        return content;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
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
