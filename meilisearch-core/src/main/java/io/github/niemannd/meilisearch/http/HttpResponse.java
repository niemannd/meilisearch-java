package io.github.niemannd.meilisearch.http;

import io.github.niemannd.meilisearch.api.MeiliAPIException;
import io.github.niemannd.meilisearch.api.MeiliException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.NameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
    private final Map<String, String> headers;
    private final int statusCode;
    private final String content;

    public HttpResponse(Map<String, String> headers, int statusCode, String body) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.content = body;
    }

    public HttpResponse(CloseableHttpResponse response) {
        this.headers = Arrays.stream(response.getHeaders()).collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
        this.statusCode = response.getCode();
        this.content = readContent(response);
    }

    private String readContent(CloseableHttpResponse response) throws MeiliException {
        try {
            if (response.getEntity() == null || response.getEntity().getContentLength() == 0)
                return null;
            return new BufferedReader(new InputStreamReader(response.getEntity().getContent())).lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw new MeiliAPIException(e);
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContent() {
        return content;
    }

    public boolean hasContent() {
        return content != null;
    }
}