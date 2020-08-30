package io.github.niemannd.meilisearch.api.instance;

import io.github.niemannd.meilisearch.api.MeiliAPIException;
import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.http.HttpClient;
import io.github.niemannd.meilisearch.http.HttpResponse;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.json.JsonProcessor;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstanceServicesTest {

    HttpClient client = mock(HttpClient.class);
    JsonProcessor processor = new JacksonJsonProcessor();
    InstanceServices classToTest = new InstanceServices(client, processor);

    @Test
    void isHealthy() {
        when(client.get(any(), any())).thenReturn(new HttpResponse(null, 200, "")).thenThrow(MeiliAPIException.class);
        assertTrue(classToTest.isHealthy());
        assertFalse(classToTest.isHealthy());
    }

    @Test
    void version() {
        when(client.get(any(), any()))
                .thenReturn(new HttpResponse(null, 200, "{\"commitSha\":\"b46889b5f0f2f8b91438a08a358ba8f05fc09fc1\",\"buildDate\":\"2019-11-15T09:51:54.278247+00:00\",\"pkgVersion\":\"0.1.1\"}"))
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
        when(client.put(any(), any(), any())).thenReturn(new HttpResponse(null, 200, "")).thenThrow(MeiliException.class);
        assertTrue(classToTest.setMaintenance(true));
        assertFalse(classToTest.setMaintenance(true));
    }
}