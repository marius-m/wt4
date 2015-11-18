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

import java.net.URI;

/**
 * Link between two JIRA issues
 *
 * @since v0.1
 */
public class IssueLink {
	private final String targetIssueKey;
	private final URI targetIssueUri;
	private final IssueLinkType issueLinkType;

	public IssueLink(String targetIssueKey, URI targetIssueUri, IssueLinkType issueLinkType) {
		this.targetIssueKey = targetIssueKey;
		this.targetIssueUri = targetIssueUri;
		this.issueLinkType = issueLinkType;
	}

	public String getTargetIssueKey() {
		return targetIssueKey;
	}

	public URI getTargetIssueUri() {
		return targetIssueUri;
	}

	public IssueLinkType getIssueLinkType() {
		return issueLinkType;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("targetIssueKey", targetIssueKey).
				add("targetIssueUri", targetIssueUri).
				add("issueLinkType", issueLinkType).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IssueLink) {
			IssueLink that = (IssueLink) obj;
			return Objects.equal(this.targetIssueKey, that.targetIssueKey)
					&& Objects.equal(this.targetIssueUri, that.targetIssueUri)
					&& Objects.equal(this.issueLinkType, that.issueLinkType);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(targetIssueKey, targetIssueUri, issueLinkType);
	}

}
