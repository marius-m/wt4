/*
 * Copyright (C) 2012 Atlassian
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
package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.jira.rest.client.api.SessionRestClient;
import com.atlassian.jira.rest.client.internal.json.SessionJsonParser;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Session;
import com.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Asynchronous implementation of SessionRestClient.
 *
 * @since v2.0
 */
public class AsynchronousSessionRestClient extends AbstractAsynchronousRestClient implements SessionRestClient {

	private final SessionJsonParser sessionJsonParser = new SessionJsonParser();
	private final URI serverUri;

	public AsynchronousSessionRestClient(final URI serverUri, final HttpClient client) {
		super(client);
		this.serverUri = serverUri;
	}

	@Override
	public Promise<Session> getCurrentSession() throws RestClientException {
		return getAndParse(UriBuilder.fromUri(serverUri).path("rest/auth/latest/session").build(), sessionJsonParser);
	}

}
