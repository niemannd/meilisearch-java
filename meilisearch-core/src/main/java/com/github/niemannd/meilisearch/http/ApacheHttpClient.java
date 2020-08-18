package com.github.niemannd.meilisearch.http;

import com.github.niemannd.meilisearch.api.MeiliError;
import com.github.niemannd.meilisearch.api.MeiliErrorException;
import com.github.niemannd.meilisearch.config.Configuration;
import com.github.niemannd.meilisearch.json.JsonProcessor;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ApacheHttpClient implements HttpClient {

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

    CloseableHttpResponse execute(ClassicHttpRequest request) throws MeiliErrorException {
        Supplier<String> keySupplier = config.getKey();
        if (keySupplier != null && keySupplier.get() != null) {
            request.addHeader("X-Meili-API-Key", keySupplier.get());
        }
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(request);
            int responseCode = response.getCode();
            if (responseCode < 200 || responseCode > 299) {
                if (response.getEntity() != null) {
                    MeiliError error = processor.deserialize(EntityUtils.toString(response.getEntity()), MeiliError.class);
                    throw new MeiliErrorException(error.getMessage(), error);
                }
            }
        } catch (ParseException | IOException e) {
            throw new MeiliErrorException(e);
        }
        return response;
    }

    @Override
    public String get(String path, Map<String, String> params) throws MeiliErrorException {
        try {
            String query = createQueryString(params);
            HttpGet request = new HttpGet(this.config.getUrl() + path + "?" + query);
            return EntityUtils.toString(execute(request).getEntity());
        } catch (IOException | ParseException e) {
            throw new MeiliErrorException(e);
        }
    }

    @Override
    public <T> String post(String path, T body) throws MeiliErrorException {
        try {
            HttpPost request = new HttpPost(this.config.getUrl() + path);
            String requestBody = processor.serialize(body);
            BasicHttpEntity basicHttpEntity = new BasicHttpEntity(
                    new ByteArrayInputStream(requestBody.getBytes()),
                    requestBody.getBytes().length,
                    ContentType.APPLICATION_JSON,
                    "UTF-8"
            );
            request.setEntity(basicHttpEntity);
            CloseableHttpResponse execute = execute(request);
            return EntityUtils.toString(execute.getEntity());
        } catch (IOException | ParseException e) {
            throw new MeiliErrorException(e);
        }
    }

    @Override
    public <T> String put(String path, Map<String, String> params, T body) throws MeiliErrorException {
        try {
            HttpPut request = new HttpPut(this.config.getUrl() + path);
            params.forEach(request::addHeader);

            BasicHttpEntity basicHttpEntity;
            if (body != null) {
                byte[] content = processor.serialize(body).getBytes();
                basicHttpEntity = new BasicHttpEntity(new ByteArrayInputStream(content), content.length, ContentType.APPLICATION_JSON, "UTF-8");
                request.setEntity(basicHttpEntity);
            }
            return EntityUtils.toString(execute(request).getEntity());
        } catch (IOException | ParseException e) {
            throw new MeiliErrorException(e);
        }
    }

    @Override
    public boolean delete(String path) {
        try {
            HttpDelete request = new HttpDelete(this.config.getUrl() + path);
            int code = execute(request).getCode();
            return code > 199 && code < 300;
        } catch (MeiliErrorException e) {
            return false;
        }
    }
}
