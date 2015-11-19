package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Request;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * Abstract wrapper for an Atlassian HttpClient.
 */
public abstract class AtlassianHttpClientDecorator implements DisposableHttpClient {

	private final HttpClient httpClient;

	public AtlassianHttpClientDecorator(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void flushCacheByUriPattern(Pattern urlPattern) {
		httpClient.flushCacheByUriPattern(urlPattern);
	}

	public Request newRequest() {
		return httpClient.newRequest();
	}

	public Request newRequest(URI uri) {
		return httpClient.newRequest(uri);
	}

	public Request newRequest(URI uri, String contentType, String entity) {
		return httpClient.newRequest(uri, contentType, entity);
	}

	public Request newRequest(String uri) {
		return httpClient.newRequest(uri);
	}

	public Request newRequest(String uri, String contentType, String entity) {
		return httpClient.newRequest(uri, contentType, entity);
	}
}
