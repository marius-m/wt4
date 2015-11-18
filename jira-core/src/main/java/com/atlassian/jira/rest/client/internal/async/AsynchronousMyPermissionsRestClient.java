/*
 * Copyright (C) 2014 Atlassian
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

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.MyPermissionsRestClient;
import com.atlassian.jira.rest.client.api.domain.Permissions;
import com.atlassian.jira.rest.client.api.domain.input.MyPermissionsInput;
import com.atlassian.jira.rest.client.internal.json.PermissionsJsonParser;
import com.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class AsynchronousMyPermissionsRestClient extends AbstractAsynchronousRestClient implements MyPermissionsRestClient {
	private static final String URI_PREFIX = "mypermissions";
	private final URI baseUri;
	private final PermissionsJsonParser permissionsJsonParser = new PermissionsJsonParser();

	protected AsynchronousMyPermissionsRestClient(final URI baseUri, final HttpClient client) {
		super(client);
		this.baseUri = baseUri;
	}

	@Override
	public Promise<Permissions> getMyPermissions(final MyPermissionsInput permissionInput) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path(URI_PREFIX);
		addContextParams(uriBuilder, permissionInput);
		return getAndParse(uriBuilder.build(), permissionsJsonParser);
	}

	private UriBuilder addContextParams(UriBuilder uriBuilder, MyPermissionsInput permissionInput) {
		if (permissionInput != null) {
			if (permissionInput.getProjectKey() != null) {
				uriBuilder.queryParam("projectKey", permissionInput.getProjectKey());
			}
			if (permissionInput.getProjectId() != null) {
				uriBuilder.queryParam("projectId", permissionInput.getProjectId());
			}
			if (permissionInput.getIssueKey() != null) {
				uriBuilder.queryParam("issueKey", permissionInput.getIssueKey());
			}
			if (permissionInput.getIssueId() != null) {
				uriBuilder.queryParam("issueId", permissionInput.getIssueId());
			}
		}
		return uriBuilder;
	}
}
