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

import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;

/**
 * The com.atlassian.jira.rest.client.api handling user resources.
 *
 * @since v0.1
 */
public interface UserRestClient {
	/**
	 * Retrieves detailed information about selected user.
	 * Try to use {@link #getUser(URI)} instead as that method is more RESTful (well connected)
	 *
	 * @param username JIRA username/login
	 * @return complete information about given user
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<User> getUser(String username);

	/**
	 * Retrieves detailed information about selected user.
	 * This method is preferred over {@link #getUser(String)} as com.atlassian.jira.rest.it's more RESTful (well connected)
	 *
	 * @param userUri URI of user resource
	 * @return complete information about given user
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<User> getUser(URI userUri);
}
