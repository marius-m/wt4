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

package com.atlassian.jira.rest.client.api.domain;

import java.net.URI;

/**
 * Information about current user "session" - or in case of the recommended stateless connection (really RESTful design)
 * just the information about the user and the user login data.
 *
 * @since v0.1
 */
public class Session {
	private final URI userUri;
	private final String username;
	private final LoginInfo loginInfo;

	public Session(URI userUri, String username, LoginInfo loginInfo) {
		this.userUri = userUri;
		this.username = username;
		this.loginInfo = loginInfo;
	}

	public URI getUserUri() {
		return userUri;
	}

	public String getUsername() {
		return username;
	}

	public LoginInfo getLoginInfo() {
		return loginInfo;
	}
}
