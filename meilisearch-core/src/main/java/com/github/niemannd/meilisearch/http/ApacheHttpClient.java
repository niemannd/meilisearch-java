package com.github.niemannd.meilisearch.http;

import com.github.niemannd.meilisearch.config.Configuration;
import com.github.niemannd.meilisearch.json.JsonProcessor;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class ApacheHttpClient implements HttpClient {
    private static final Logger log = getLogger(ApacheHttpClient.class);

    private final CloseableHttpClient httpClient;
    private final Configuration config;
    private final JsonProcessor processor;

    public ApacheHttpClient(Configuration config, JsonProcessor processor) {
        this.httpClient = HttpClients.createDefault();
        this.processor = processor;
        this.config = config;
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

    @Override
    public String get(String path, Map<String, String> params) {
        try {
            String query = createQueryString(params);
            HttpGet request = new HttpGet(this.config.getUrl() + path + "?" + query);
            return EntityUtils.toString(httpClient.execute(request).getEntity());
        } catch (IOException | ParseException e) {
            log.error("Error sending http get request to '{}'", this.config.getUrl() + path, e);
        }
        return null;
    }

    @Override
    public <T> String post(String path, T body) {
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
            CloseableHttpResponse execute = httpClient.execute(request);
            return EntityUtils.toString(execute.getEntity());
        } catch (IOException | ParseException e) {
            log.error("Error sending http post request to '{}'", this.config.getUrl() + path, e);
        }
        return null;
    }

    @Override
    public <T> String put(String path, Map<String, String> params, T body) {
        try {
            HttpPut request = new HttpPut(this.config.getUrl() + path);
            params.forEach(request::addHeader);

            BasicHttpEntity basicHttpEntity;
            if (body != null) {
                byte[] content = processor.serialize(body).getBytes();
                basicHttpEntity = new BasicHttpEntity(new ByteArrayInputStream(content), content.length, ContentType.APPLICATION_JSON, "UTF-8");
                request.setEntity(basicHttpEntity);
            }
            return EntityUtils.toString(httpClient.execute(request).getEntity());
        } catch (IOException | ParseException e) {
            log.error("Error sending http put request to '{}'", this.config.getUrl() + path, e);
        }
        return null;
    }

    @Override
    public boolean delete(String path) {
        try {
            HttpDelete request = new HttpDelete(this.config.getUrl() + path);
            int code = httpClient.execute(request).getCode();
            return code > 199 && code < 300;
        } catch (IOException e) {
            log.error("Error sending http delete request to '{}'", this.config.getUrl() + path, e);
        }
        return false;
    }
}
