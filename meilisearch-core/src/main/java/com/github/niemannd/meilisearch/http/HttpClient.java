package com.github.niemannd.meilisearch.http;


import java.io.IOException;
import java.util.Map;

public interface HttpClient {

    String get(String path, Map<String, String> params);

    <T> String post(String path, T body);

    <T> String put(String path, Map<String, String> params, T body);

    boolean delete(String path);

}
