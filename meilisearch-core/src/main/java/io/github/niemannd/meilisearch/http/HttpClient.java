package io.github.niemannd.meilisearch.http;


import io.github.niemannd.meilisearch.api.MeiliException;

import java.util.Map;

public interface HttpClient<B> {

    HttpResponse<B> get(String path, Map<String, String> params) throws MeiliException;

    <T> HttpResponse<B> post(String path, T body) throws MeiliException;

    <T> HttpResponse<B> put(String path, Map<String, String> params, T body) throws MeiliException;

    HttpResponse<B> delete(String path);

}
