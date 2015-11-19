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
import java.net.URI;

/**
 * Basic information about JIRA server
 *
 * @since v0.1
 */
public class ServerInfo {
	private final URI baseUri;
	private final String version;
	private final int buildNumber;
	private final DateTime buildDate;
	@Nullable
	private final DateTime serverTime;
	private final String scmInfo;
	private final String serverTitle;

	public ServerInfo(URI baseUri, String version, int buildNumber, DateTime buildDate, @Nullable DateTime serverTime,
			String scmInfo, String serverTitle) {
		this.baseUri = baseUri;
		this.version = version;
		this.buildNumber = buildNumber;
		this.buildDate = buildDate;
		this.serverTime = serverTime;
		this.scmInfo = scmInfo;
		this.serverTitle = serverTitle;
	}

	/**
	 * @return base URI of this JIRA instance
	 */
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * @return version of this JIRA instance (like "4.2.1")
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return build number
	 */
	public int getBuildNumber() {
		return buildNumber;
	}

	/**
	 * @return date when the version of this JIRA instance has been built
	 */
	public DateTime getBuildDate() {
		return buildDate;
	}

	/**
	 * @return current time (when the response is generated) on the server side or <code>null</code>
	 *         when the user is not authenticated.
	 */
	@Nullable
	public DateTime getServerTime() {
		return serverTime;
	}

	/**
	 * @return SCM information (like SVN revision) indicated from which sources this JIRA server has been built.
	 */
	public String getScmInfo() {
		return scmInfo;
	}

	/**
	 * @return name of this JIRA instance (as defined by JIRA admin)
	 */
	public String getServerTitle() {
		return serverTitle;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(super.toString()).
				add("baseUri", baseUri).
				add("version", version).
				add("buildNumber", buildNumber).
				add("buildDate", buildDate).
				add("serverTime", serverTime).
				add("svnRevision", scmInfo).
				add("serverTitle", serverTitle).
				toString();
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServerInfo) {
			ServerInfo that = (ServerInfo) obj;
			return Objects.equal(this.baseUri, that.baseUri)
					&& Objects.equal(this.version, that.version)
					&& Objects.equal(this.buildNumber, that.buildNumber)
					&& Objects.equal(this.buildDate, that.buildDate)
					&& Objects.equal(this.serverTime, that.serverTime)
					&& Objects.equal(this.scmInfo, that.scmInfo)
					&& Objects.equal(this.serverTitle, that.serverTitle);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(baseUri, version, buildNumber, buildDate, serverTime, scmInfo, serverTitle);
	}

}
