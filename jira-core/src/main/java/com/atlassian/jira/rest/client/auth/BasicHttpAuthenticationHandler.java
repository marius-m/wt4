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

package com.atlassian.jira.rest.client.auth;

import com.atlassian.httpclient.api.Request;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import org.apache.commons.codec.binary.Base64;

/**
 * Handler for HTTP basic authentication.
 * Do NOT use it in with unencrypted HTTP protocol over public networks, as credentials are passed
 * effectively in free text.
 *
 * @since v0.1
 */
public class BasicHttpAuthenticationHandler implements AuthenticationHandler {

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private final String username;
	private final String password;

	public BasicHttpAuthenticationHandler(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void configure(final Request request) {
		request.setHeader(AUTHORIZATION_HEADER, "Basic " + encodeCredentials());
	}

	private String encodeCredentials() {
		byte[] credentials = (username + ':' + password).getBytes();
		return new String(Base64.encodeBase64(credentials));
	}

}
