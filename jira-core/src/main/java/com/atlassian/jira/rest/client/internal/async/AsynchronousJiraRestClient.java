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

import com.atlassian.jira.rest.client.api.*;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Asynchronous implementation of JIRA REST com.atlassian.jira.rest.client.
 *
 * @since v2.0
 */
public class AsynchronousJiraRestClient implements JiraRestClient {

	private final IssueRestClient issueRestClient;
	private final SessionRestClient sessionRestClient;
	private final UserRestClient userRestClient;
	private final ProjectRestClient projectRestClient;
	private final ComponentRestClient componentRestClient;
	private final MetadataRestClient metadataRestClient;
	private final SearchRestClient searchRestClient;
	private final VersionRestClient versionRestClient;
	private final ProjectRolesRestClient projectRolesRestClient;
	private final MyPermissionsRestClient myPermissionsRestClient;
	private final DisposableHttpClient httpClient;
    private final AuditRestClient auditRestClient;

    public AsynchronousJiraRestClient(final URI serverUri, final DisposableHttpClient httpClient) {
		final URI baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build();

		this.httpClient = httpClient;
		metadataRestClient = new AsynchronousMetadataRestClient(baseUri, httpClient);
		sessionRestClient = new AsynchronousSessionRestClient(serverUri, httpClient);
		issueRestClient = new AsynchronousIssueRestClient(baseUri, httpClient, sessionRestClient, metadataRestClient);
		userRestClient = new AsynchronousUserRestClient(baseUri, httpClient);
		projectRestClient = new AsynchronousProjectRestClient(baseUri, httpClient);
		componentRestClient = new AsynchronousComponentRestClient(baseUri, httpClient);
		searchRestClient = new AsynchronousSearchRestClient(baseUri, httpClient);
		versionRestClient = new AsynchronousVersionRestClient(baseUri, httpClient);
		projectRolesRestClient = new AsynchronousProjectRolesRestClient(serverUri, httpClient);
		myPermissionsRestClient = new AsynchronousMyPermissionsRestClient(baseUri, httpClient);
        auditRestClient = new AsynchronousAuditRestClient(httpClient, baseUri);
    }

	@Override
	public IssueRestClient getIssueClient() {
		return issueRestClient;
	}

	@Override
	public SessionRestClient getSessionClient() {
		return sessionRestClient;
	}

	@Override
	public UserRestClient getUserClient() {
		return userRestClient;
	}

	@Override
	public ProjectRestClient getProjectClient() {
		return projectRestClient;
	}

	@Override
	public ComponentRestClient getComponentClient() {
		return componentRestClient;
	}

	@Override
	public MetadataRestClient getMetadataClient() {
		return metadataRestClient;
	}

	@Override
	public SearchRestClient getSearchClient() {
		return searchRestClient;
	}

	@Override
	public VersionRestClient getVersionRestClient() {
		return versionRestClient;
	}

	@Override
	public ProjectRolesRestClient getProjectRolesRestClient() {
		return projectRolesRestClient;
	}

	@Override
	public MyPermissionsRestClient getMyPermissionsRestClient() {
		return myPermissionsRestClient;
	}

    @Override
    public AuditRestClient getAuditRestClient() {
        return auditRestClient;
    }

    @Override
	public void close() throws IOException {
		try {
			httpClient.destroy();
		} catch (Exception e) {
			throw (e instanceof IOException) ? ((IOException) e) : new IOException(e);
		}
	}
}

