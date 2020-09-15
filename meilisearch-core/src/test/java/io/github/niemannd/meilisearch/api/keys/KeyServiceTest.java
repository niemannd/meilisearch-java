package io.github.niemannd.meilisearch.api.keys;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.niemannd.meilisearch.http.ApacheHttpClient;
import io.github.niemannd.meilisearch.http.response.BasicHttpResponse;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeyServiceTest {
    private final HttpClient<String> client = mock(ApacheHttpClient.class);
    private final JsonProcessor processor = new JacksonJsonProcessor(new ObjectMapper());
    private final KeyService classToTest = new KeyService(client, processor);


    @Test
    void get() {
        when(client.get(any(), any())).thenReturn(new BasicHttpResponse(null, 200, "{\"private\":\"8c222193c4dff5a19689d637416820bc623375f2ad4c31a2e3a76e8f4c70440d\",\"public\":\"948413b6667024a0704c2023916c21eaf0a13485a586c43e4d2df520852a4fb8\"}"));
        Map<String, String> keys = classToTest.get();
        assertIterableEquals(Stream.of("private", "public").collect(Collectors.toList()), keys.keySet());
    }
}