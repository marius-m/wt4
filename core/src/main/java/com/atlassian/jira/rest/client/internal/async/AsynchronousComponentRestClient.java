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

import com.atlassian.jira.rest.client.api.ComponentRestClient;
import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.api.domain.input.ComponentInput;
import com.atlassian.jira.rest.client.internal.domain.input.ComponentInputWithProjectKey;
import com.atlassian.jira.rest.client.internal.json.ComponentJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.gen.ComponentInputWithProjectKeyJsonGenerator;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.util.concurrent.Promise;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Asynchronous implementation of ComponentRestClient.
 *
 * @since v2.0
 */
public class AsynchronousComponentRestClient extends AbstractAsynchronousRestClient implements ComponentRestClient {

	private final ComponentJsonParser componentJsonParser = new ComponentJsonParser();
	private final URI componentUri;

	public AsynchronousComponentRestClient(final URI baseUri, final HttpClient client) {
		super(client);
		componentUri = UriBuilder.fromUri(baseUri).path("component").build();
	}

	@Override
	public Promise<Component> getComponent(final URI componentUri) {
		return getAndParse(componentUri, componentJsonParser);
	}

	@Override
	public Promise<Component> createComponent(final String projectKey, final ComponentInput componentInput) {
		final ComponentInputWithProjectKey helper = new ComponentInputWithProjectKey(projectKey, componentInput);
		return postAndParse(componentUri, helper, new ComponentInputWithProjectKeyJsonGenerator(), componentJsonParser);
	}

	@Override
	public Promise<Component> updateComponent(URI componentUri, ComponentInput componentInput) {
		final ComponentInputWithProjectKey helper = new ComponentInputWithProjectKey(null, componentInput);
		return putAndParse(componentUri, helper, new ComponentInputWithProjectKeyJsonGenerator(), componentJsonParser);
	}

	@Override
	public Promise<Void> removeComponent(URI componentUri, @Nullable URI moveIssueToComponentUri) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(componentUri);
		if (moveIssueToComponentUri != null) {
			uriBuilder.queryParam("moveIssuesTo", moveIssueToComponentUri);
		}
		return delete(uriBuilder.build());
	}

	@Override
	public Promise<Integer> getComponentRelatedIssuesCount(URI componentUri) {
		final URI relatedIssueCountsUri = UriBuilder.fromUri(componentUri).path("relatedIssueCounts").build();
		return getAndParse(relatedIssueCountsUri, new JsonObjectParser<Integer>() {
			@Override
			public Integer parse(JSONObject json) throws JSONException {
				return json.getInt("issueCount");
			}
		});
	}
}
