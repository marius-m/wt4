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

import com.atlassian.jira.rest.client.api.ProjectRolesRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProjectRole;
import com.atlassian.jira.rest.client.api.domain.ProjectRole;
import com.atlassian.jira.rest.client.internal.json.BasicProjectRoleJsonParser;
import com.atlassian.jira.rest.client.internal.json.ProjectRoleJsonParser;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.util.concurrent.Promise;
import com.atlassian.util.concurrent.Promises;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collection;

/**
 * Asynchronous implementation of ProjectRolesRestClient.
 *
 * @since v2.0
 */
public class AsynchronousProjectRolesRestClient extends AbstractAsynchronousRestClient implements ProjectRolesRestClient {

	private final ProjectRoleJsonParser projectRoleJsonParser;
	private final BasicProjectRoleJsonParser basicRoleJsonParser;

	public AsynchronousProjectRolesRestClient(final URI serverUri, final HttpClient client) {
		super(client);
		this.projectRoleJsonParser = new ProjectRoleJsonParser(serverUri);
		this.basicRoleJsonParser = new BasicProjectRoleJsonParser();
	}

	@Override
	public Promise<ProjectRole> getRole(URI uri) {
		return getAndParse(uri, projectRoleJsonParser);
	}

	@Override
	public Promise<ProjectRole> getRole(final URI projectUri, final Long roleId) {
		final URI roleUri = UriBuilder
				.fromUri(projectUri)
				.path("role")
				.path(String.valueOf(roleId))
				.build();
		return getAndParse(roleUri, projectRoleJsonParser);
	}

	@Override
	public Promise<Iterable<ProjectRole>> getRoles(final URI projectUri) {
		final URI rolesUris = UriBuilder
				.fromUri(projectUri)
				.path("role")
				.build();
		final Promise<Collection<BasicProjectRole>> basicProjectRoles = getAndParse(rolesUris, basicRoleJsonParser);

		return Promises.promise(Iterables.transform(basicProjectRoles.claim(), new Function<BasicProjectRole, ProjectRole>() {
			@Override
			public ProjectRole apply(final BasicProjectRole basicProjectRole) {
				return getRole(basicProjectRole.getSelf()).claim();
			}
		}));
	}
}
