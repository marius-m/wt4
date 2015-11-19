/*
 * Copyright (C) 2010 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.api;

import com.atlassian.httpclient.api.HttpClient;

import java.net.URI;

/**
 * Factory for producing JIRA REST com.atlassian.jira.client with selected authentication handler
 *
 * @since v0.1
 */
public interface JiraRestClientFactory {

	/**
	 * Creates an instance of JiraRestClient with default HttpClient settings.
	 *
	 * @param serverUri             - URI of JIRA instance.
	 * @param authenticationHandler - requests authenticator.
	 */
	JiraRestClient create(final URI serverUri, final AuthenticationHandler authenticationHandler);

	/**
	 * Creates an instance of JiraRestClient with default HttpClient settings. HttpClient will conduct a
	 * basic authentication for given credentials.
	 *
	 * @param serverUri - URI or JIRA instance.
	 * @param username  - username of the user used to log in to JIRA.
	 * @param password  - password of the user used to log in to JIRA.
	 */
	JiraRestClient createWithBasicHttpAuthentication(final URI serverUri, final String username, final String password);

	/**
	 * Creates an instance of JiraRestClient with given Atlassian HttpClient.
	 * Please note, that this com.atlassian.jira.rest.client.api has to be fully configured to do the request authentication.
	 *
	 * @param serverUri  - URI of JIRA instance.
	 * @param httpClient - instance of Atlassian HttpClient.
	 */
	JiraRestClient create(final URI serverUri, final HttpClient httpClient);
}
