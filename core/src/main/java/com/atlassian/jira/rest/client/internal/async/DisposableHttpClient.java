package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.httpclient.api.HttpClient;

/**
 * Atlassian HttpClient with destroy exposed.
 */
public interface DisposableHttpClient extends HttpClient {

	void destroy() throws Exception;

}
