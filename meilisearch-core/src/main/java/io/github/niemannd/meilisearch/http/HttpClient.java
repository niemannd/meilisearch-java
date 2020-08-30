package io.github.niemannd.meilisearch.http;


import io.github.niemannd.meilisearch.api.MeiliException;

import java.util.Map;

public interface HttpClient {

    HttpResponse get(String path, Map<String, String> params) throws MeiliException;

    <T> HttpResponse post(String path, T body) throws MeiliException;

    <T> HttpResponse put(String path, Map<String, String> params, T body) throws MeiliException;

    HttpResponse delete(String path);

}
