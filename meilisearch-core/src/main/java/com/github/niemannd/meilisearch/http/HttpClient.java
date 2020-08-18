package com.github.niemannd.meilisearch.http;


import com.github.niemannd.meilisearch.api.MeiliErrorException;

import java.io.IOException;
import java.util.Map;

public interface HttpClient {

    String get(String path, Map<String, String> params) throws MeiliErrorException;

    <T> String post(String path, T body) throws MeiliErrorException;

    <T> String put(String path, Map<String, String> params, T body) throws MeiliErrorException;

    boolean delete(String path);

}
