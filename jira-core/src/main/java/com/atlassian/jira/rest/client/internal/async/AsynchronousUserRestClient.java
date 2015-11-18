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

import com.atlassian.jira.rest.client.api.UserRestClient;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.json.UserJsonParser;
import com.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Asynchronous implementation of UserRestClient.
 *
 * @since v2.0
 */
public class AsynchronousUserRestClient extends AbstractAsynchronousRestClient implements UserRestClient {

	private static final String USER_URI_PREFIX = "user";
	private final UserJsonParser userJsonParser = new UserJsonParser();

	private final URI baseUri;

	public AsynchronousUserRestClient(final URI baseUri, final HttpClient client) {
		super(client);
		this.baseUri = baseUri;
	}

	@Override
	public Promise<User> getUser(final String username) {
		final URI userUri = UriBuilder.fromUri(baseUri).path(USER_URI_PREFIX)
				.queryParam("username", username).queryParam("expand", "groups").build();
		return getUser(userUri);
	}

	@Override
	public Promise<User> getUser(final URI userUri) {
		return getAndParse(userUri, userJsonParser);
	}
}
