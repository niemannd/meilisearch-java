package io.github.niemannd.meilisearch.http;

import io.github.niemannd.meilisearch.api.MeiliAPIException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpResponseTest {

    private HttpResponse classToTest;

    public HttpResponseTest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Test", "TestTest");
        classToTest = new HttpResponse(headers, 200, "{}");
    }

    @Test
    void getHeaders() {
        assertThat(classToTest.getHeaders(), aMapWithSize(1));
        assertThat(classToTest.getHeaders().keySet(), containsInAnyOrder("Test"));
        assertThat(classToTest.getHeaders().get("Test"), is("TestTest"));
    }

    @Test
    void getStatusCode() {
        assertThat(classToTest.getStatusCode(), is(200));
    }

    @Test
    void getContent() {
        assertThat(classToTest.getContent(), is("{}"));
    }

    @Test
    void hasContent() {
        assertThat(classToTest.hasContent(), is(true));
    }

    @Test
    void readContent() {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getHeaders()).thenReturn(new Header[0]);
        when(response.getCode()).thenReturn(200);
        when(response.getEntity()).thenAnswer(invocationOnMock -> {
            throw new IOException("oh boy!");
        });
        assertThrows(MeiliAPIException.class, () -> new HttpResponse(response));
    }
}