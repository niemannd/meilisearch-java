package io.github.niemannd.meilisearch.http;


import io.github.niemannd.meilisearch.api.MeiliException;

import java.util.Map;

public interface HttpClient {

    String get(String path, Map<String, String> params) throws MeiliException;

    <T> String post(String path, T body) throws MeiliException;

    <T> String put(String path, Map<String, String> params, T body) throws MeiliException;

    boolean delete(String path);

}
