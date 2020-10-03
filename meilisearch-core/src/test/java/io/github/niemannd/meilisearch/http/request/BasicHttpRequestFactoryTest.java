package io.github.niemannd.meilisearch.http.request;

import io.github.niemannd.meilisearch.ServiceTemplate;
import io.github.niemannd.meilisearch.http.HttpMethod;
import io.github.niemannd.meilisearch.json.JacksonJsonProcessor;
import io.github.niemannd.meilisearch.utils.Movie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasicHttpRequestFactoryTest {

    private final ServiceTemplate mock = mock(ServiceTemplate.class);
    private final BasicHttpRequestFactory classToTest = new BasicHttpRequestFactory(mock);

    @Test
    void create() {
        when(mock.getProcessor()).thenReturn(new JacksonJsonProcessor());
        BasicHttpRequest httpRequest = (BasicHttpRequest) classToTest.create(HttpMethod.GET, "/testpath", null, null);
        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/testpath", httpRequest.getPath());
        assertEquals(new HashMap<>(), httpRequest.getHeaders());
        assertNull(httpRequest.getContent());

        httpRequest = (BasicHttpRequest) classToTest.create(HttpMethod.GET, "/testpath", new Movie(), null);
        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/testpath", httpRequest.getPath());
        assertEquals(new HashMap<>(), httpRequest.getHeaders());
        assertEquals("{\"id\":0.0,\"title\":null,\"poster\":null,\"overview\":null,\"genre\":null,\"release_date\":null}", httpRequest.getContent());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("test", "test");
        httpRequest = (BasicHttpRequest) classToTest.create(HttpMethod.GET, "/testpath", "null", headers);
        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/testpath", httpRequest.getPath());
        assertThat(httpRequest.getHeaders(), Matchers.hasEntry("test", "test"));
        assertEquals("null", httpRequest.getContent());

    }
}