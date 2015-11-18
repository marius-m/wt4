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

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;

/**
 * The com.atlassian.jira.rest.client.api handling project resources.
 *
 * @since v0.1
 */
public interface ProjectRestClient {
	/**
	 * Retrieves complete information about given project.
	 *
	 * @param key unique key of the project (usually 2+ characters)
	 * @return complete information about given project
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<Project> getProject(String key);

	/**
	 * Retrieves complete information about given project.
	 * Use this method rather than {@link ProjectRestClient#getProject(String)}
	 * wheever you can, as this method is proof for potential changes of URI scheme used for exposing various
	 * resources by JIRA REST API.
	 *
	 * @param projectUri URI to project resource (usually get from <code>self</code> attribute describing component elsewhere
	 * @return complete information about given project
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<Project> getProject(URI projectUri);

	/**
	 * Returns all projects, which are visible for the currently logged in user. If no user is logged in, it returns the
	 * list of projects that are visible when using anonymous access.
	 *
	 * @return projects which the currently logged user can see
	 * @since com.atlassian.jira.rest.client.api: 0.2, server 4.3
	 */
	Promise<Iterable<BasicProject>> getAllProjects();

}
