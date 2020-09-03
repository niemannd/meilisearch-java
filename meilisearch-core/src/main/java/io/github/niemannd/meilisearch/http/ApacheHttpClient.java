package io.github.niemannd.meilisearch.http;

import io.github.niemannd.meilisearch.api.MeiliAPIException;
import io.github.niemannd.meilisearch.api.MeiliError;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.config.Configuration;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ApacheHttpClient implements HttpClient<String> {

    private final CloseableHttpClient httpClient;
    private final Configuration config;
    private final JsonProcessor processor;

    public ApacheHttpClient(Configuration config, JsonProcessor processor) {
        this(HttpClients.createDefault(), config, processor);
    }

    public ApacheHttpClient(CloseableHttpClient httpClient, Configuration config, JsonProcessor processor) {
        this.httpClient = httpClient;
        this.processor = processor;
        this.config = config;
    }

    String createQueryString(Map<String, String> params) {
        return params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    HttpResponse<String>execute(ClassicHttpRequest request) throws MeiliException {
        Supplier<String> keySupplier = config.getKey();
        if (keySupplier != null && keySupplier.get() != null) {
            request.addHeader("X-Meili-API-Key", keySupplier.get());
        }
        BasicHttpResponse response;
        try {
            CloseableHttpResponse nativeResponse = httpClient.execute(request);
            response = new BasicHttpResponse(nativeResponse);
            nativeResponse.close();
            int responseCode = response.getStatusCode();
            if (responseCode < 200 || responseCode > 299) {
                if(response.hasContent()) {
                    MeiliError error = processor.deserialize(response.getContent(), MeiliError.class);
                    throw new MeiliAPIException(error.getMessage(), error);
                } else {
                    throw new MeiliAPIException("empty response without success code");
                }
            }
        } catch (IOException e) {
            throw new MeiliAPIException(e);
        }
        return response;
    }

    @Override
    public HttpResponse<String> get(String path, Map<String, String> params) throws MeiliException {
        String query = createQueryString(params);
        HttpGet request = new HttpGet(this.config.getUrl() + path + "?" + query);
        return execute(request);
    }

    @Override
    public <T> HttpResponse<String> post(String path, T body) throws MeiliException {
        HttpPost request = new HttpPost(this.config.getUrl() + path);
        String requestBody = processor.serialize(body);
        BasicHttpEntity basicHttpEntity = new BasicHttpEntity(
                new ByteArrayInputStream(requestBody.getBytes()),
                requestBody.getBytes().length,
                ContentType.APPLICATION_JSON,
                "UTF-8"
        );
        request.setEntity(basicHttpEntity);
        return execute(request);
    }

    @Override
    public <T> HttpResponse<String> put(String path, Map<String, String> params, T body) throws MeiliException {
        HttpPut request = new HttpPut(this.config.getUrl() + path);
        params.forEach(request::addHeader);

        BasicHttpEntity basicHttpEntity;
        if (body != null) {
            byte[] content = processor.serialize(body).getBytes();
            basicHttpEntity = new BasicHttpEntity(new ByteArrayInputStream(content), content.length, ContentType.APPLICATION_JSON, "UTF-8");
            request.setEntity(basicHttpEntity);
        }
        return execute(request);
    }

    @Override
    public HttpResponse<String> delete(String path) throws MeiliException {
        HttpDelete request = new HttpDelete(this.config.getUrl() + path);
        return execute(request);
    }
}
