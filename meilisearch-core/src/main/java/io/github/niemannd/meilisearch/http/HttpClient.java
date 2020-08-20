package io.github.niemannd.meilisearch.http;


import io.github.niemannd.meilisearch.api.MeiliAPIException;

import java.util.Map;

public interface HttpClient {

    String get(String path, Map<String, String> params) throws MeiliAPIException;

    <T> String post(String path, T body) throws MeiliAPIException;

    <T> String put(String path, Map<String, String> params, T body) throws MeiliAPIException;

    boolean delete(String path);

}
