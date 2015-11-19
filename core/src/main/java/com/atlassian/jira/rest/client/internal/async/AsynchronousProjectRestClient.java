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

import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.internal.json.BasicProjectsJsonParser;
import com.atlassian.jira.rest.client.internal.json.ProjectJsonParser;
import com.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Asynchronous implementation of ProjectRestClient.
 *
 * @since v2.0
 */
public class AsynchronousProjectRestClient extends AbstractAsynchronousRestClient implements ProjectRestClient {

	private static final String PROJECT_URI_PREFIX = "project";
	private final ProjectJsonParser projectJsonParser = new ProjectJsonParser();
	private final BasicProjectsJsonParser basicProjectsJsonParser = new BasicProjectsJsonParser();

	private final URI baseUri;

	public AsynchronousProjectRestClient(final URI baseUri, final HttpClient client) {
		super(client);
		this.baseUri = baseUri;
	}

	@Override
	public Promise<Project> getProject(final String key) {
		final URI uri = UriBuilder.fromUri(baseUri).path(PROJECT_URI_PREFIX).path(key).build();
		return getAndParse(uri, projectJsonParser);
	}

	@Override
	public Promise<Project> getProject(final URI projectUri) {
		return getAndParse(projectUri, projectJsonParser);
	}

	@Override
	public Promise<Iterable<BasicProject>> getAllProjects() {
		final URI uri = UriBuilder.fromUri(baseUri).path(PROJECT_URI_PREFIX).build();
		return getAndParse(uri, basicProjectsJsonParser);
	}
}
