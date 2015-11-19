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
package com.atlassian.jira.rest.client.api;

import com.atlassian.jira.rest.client.api.domain.ProjectRole;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;

/**
 * The com.atlassian.jira.rest.client.api handling roles resources.
 *
 * @since 1.0
 */
public interface ProjectRolesRestClient {

	/**
	 * Retrieves a full information about the selected role.
	 *
	 * @param uri URI of the role to retrieve.
	 * @return full information about selected role.
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<ProjectRole> getRole(URI uri);

	/**
	 * Retrieves a full information about the selected role.
	 *
	 * @param projectUri uri of the project of the role to retrieve.
	 * @param roleId     unique role id.
	 * @return full information about selected role.
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<ProjectRole> getRole(URI projectUri, Long roleId);

	/**
	 * Retrieves a collection of roles in the selected project.
	 *
	 * @param projectUri uri of the project of the roles to retrieve.
	 * @return a collection of roles in the selected project.
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<Iterable<ProjectRole>> getRoles(URI projectUri);

}
