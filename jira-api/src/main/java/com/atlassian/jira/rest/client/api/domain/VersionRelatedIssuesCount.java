/*
 * Copyright (C) 2011 Atlassian
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

import java.net.URI;

/**
 * Represents number of issues which use given version in their FixVersion(s) and Affects Version fields.
 * This is mostly useful when presenting per-version basic issue stats or when about to remove given version completely.
 *
 * @since com.atlassian.jira.rest.client.api 0.3, server 4.4
 */
public class VersionRelatedIssuesCount {

	private URI versionUri;

	private final int numFixedIssues;

	private final int numAffectedIssues;

	public VersionRelatedIssuesCount(URI versionUri, int numFixedIssues, int numAffectedIssues) {
		this.versionUri = versionUri;
		this.numAffectedIssues = numAffectedIssues;
		this.numFixedIssues = numFixedIssues;
	}

	/**
	 * @return link to Version entity this object describes issue stats for
	 */
	public URI getVersionUri() {
		return versionUri;
	}

	/**
	 * @return number of issues which have this version set in their Fix Version(s) field
	 *         (as a solely set version or one of multiple values set)
	 */
	public int getNumFixedIssues() {
		return numFixedIssues;
	}

	/**
	 * @return number of issues which have this version set in their Affects Version(s) field
	 *         (as a solely set version or one of multiple values set)
	 */
	public int getNumAffectedIssues() {
		return numAffectedIssues;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("versionUri", versionUri).
				add("numFixedIssues", numFixedIssues).
				add("numAffectedIssues", numAffectedIssues).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VersionRelatedIssuesCount) {
			VersionRelatedIssuesCount that = (VersionRelatedIssuesCount) obj;
			return Objects.equal(this.numFixedIssues, that.numFixedIssues)
					&& Objects.equal(this.versionUri, that.versionUri)
					&& Objects.equal(this.numAffectedIssues, that.numAffectedIssues);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(versionUri, numAffectedIssues, numFixedIssues);
	}

}
