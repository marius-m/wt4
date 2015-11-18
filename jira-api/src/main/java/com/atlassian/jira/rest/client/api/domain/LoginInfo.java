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
import org.joda.time.DateTime;

import javax.annotation.Nullable;

/**
 * Statistics about logins (successful and failed logins number and last date) for the current user
 *
 * @since v0.1
 */
public class LoginInfo {
	private final int failedLoginCount;
	private final int loginCount;
	@Nullable
	private final DateTime lastFailedLoginDate;
	@Nullable
	private final DateTime previousLoginDate;

	public LoginInfo(int failedLoginCount, int loginCount, @Nullable DateTime lastFailedLoginDate, @Nullable DateTime previousLoginDate) {
		this.failedLoginCount = failedLoginCount;
		this.loginCount = loginCount;
		this.lastFailedLoginDate = lastFailedLoginDate;
		this.previousLoginDate = previousLoginDate;
	}


	public int getFailedLoginCount() {
		return failedLoginCount;
	}

	public int getLoginCount() {
		return loginCount;
	}

	@Nullable
	public DateTime getLastFailedLoginDate() {
		return lastFailedLoginDate;
	}

	@Nullable
	public DateTime getPreviousLoginDate() {
		return previousLoginDate;
	}


	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("failedLoginCount", failedLoginCount).
				add("loginCount", loginCount).
				add("lastFailedLoginDate", lastFailedLoginDate).
				add("previousLoginDate", previousLoginDate).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LoginInfo) {
			LoginInfo that = (LoginInfo) obj;
			return Objects.equal(this.failedLoginCount, that.failedLoginCount)
					&& Objects.equal(this.loginCount, that.loginCount)
					&& Objects.equal(this.lastFailedLoginDate, that.lastFailedLoginDate)
					&& Objects.equal(this.previousLoginDate, that.previousLoginDate);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(failedLoginCount, loginCount, lastFailedLoginDate, previousLoginDate);
	}

}
