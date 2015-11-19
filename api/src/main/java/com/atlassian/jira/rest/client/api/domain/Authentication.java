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

import com.google.common.base.Objects;

/**
 * Basic Authentication information of the current user session (if the connection maintains the session)
 * or just authentication info from the last remote call (when the connection is stateless - usually
 * recommended for really RESTful designs).
 *
 * @since v0.1
 */
public class Authentication {
	private final LoginInfo loginInfo;
	private final SessionCookie sessionCookie;

	public Authentication(LoginInfo loginInfo, SessionCookie sessionCookie) {
		this.loginInfo = loginInfo;
		this.sessionCookie = sessionCookie;
	}

	public LoginInfo getLoginInfo() {
		return loginInfo;
	}

	public SessionCookie getSession() {
		return sessionCookie;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(super.toString()).
				add("loginInfo", loginInfo).
				add("sessionCookie", sessionCookie).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Authentication) {
			Authentication that = (Authentication) obj;
			return Objects.equal(this.loginInfo, that.loginInfo)
					&& Objects.equal(this.sessionCookie, that.sessionCookie);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(loginInfo, sessionCookie);
	}

}
