package io.github.niemannd.meilisearch.api.instance;

import io.github.niemannd.meilisearch.GenericServiceTemplate;
import io.github.niemannd.meilisearch.api.MeiliAPIException;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.ApacheHttpClient;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequestFactory;
import io.github.niemannd.meilisearch.http.response.BasicHttpResponse;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstanceServicesTest {

    private final HttpClient<String> client = mock(ApacheHttpClient.class);
    JsonProcessor processor = new JacksonJsonProcessor();
    private final GenericServiceTemplate serviceTemplate = new GenericServiceTemplate(client, processor);
    InstanceServices classToTest = new InstanceServices(serviceTemplate, new BasicHttpRequestFactory(serviceTemplate));

    @Test
    void isHealthy() {
        when(client.get(any(), any())).thenReturn(new BasicHttpResponse(null, 200, "")).thenThrow(MeiliAPIException.class);
        assertTrue(classToTest.isHealthy());
        assertFalse(classToTest.isHealthy());
    }

    @Test
    void version() {
        when(client.get(any(), any()))
                .thenReturn(new BasicHttpResponse(null, 200, "{\"commitSha\":\"b46889b5f0f2f8b91438a08a358ba8f05fc09fc1\",\"buildDate\":\"2019-11-15T09:51:54.278247+00:00\",\"pkgVersion\":\"0.1.1\"}"))
                .thenThrow(MeiliException.class);
        Map<String, String> version = classToTest.getVersion();
        assertNotNull(version);
        assertTrue(version.containsKey("commitSha"));
        assertEquals("b46889b5f0f2f8b91438a08a358ba8f05fc09fc1", version.get("commitSha"));
        assertTrue(version.containsKey("buildDate"));
        assertEquals("2019-11-15T09:51:54.278247+00:00", version.get("buildDate"));
        assertTrue(version.containsKey("pkgVersion"));
        assertEquals("0.1.1", version.get("pkgVersion"));
        version = classToTest.getVersion();
        assertNotNull(version);
        assertEquals(0, version.size());
    }

    @Test
    void maintenance() {
        final AtomicReference<String> body = new AtomicReference<>();
        when(client.put(any(), any(), any(String.class)))
                .thenAnswer(invocationOnMock -> {
                    body.set(invocationOnMock.getArgument(2).toString());
                    return new BasicHttpResponse(null, 200, "");
                })
                .thenAnswer(invocationOnMock -> {
                    body.set(invocationOnMock.getArgument(2).toString());
                    throw new MeiliException();
                })
                .thenAnswer(invocationOnMock -> {
                    body.set(invocationOnMock.getArgument(2).toString());
                    return new BasicHttpResponse(null, 200, "");
                });

        assertTrue(classToTest.setMaintenance(true));
        assertEquals("{\"health\": true }", body.get());
        assertFalse(classToTest.setMaintenance(true));
        assertEquals("{\"health\": true }", body.get());
        assertTrue(classToTest.setMaintenance(false));
        assertEquals("{\"health\": false }", body.get());
    }
}