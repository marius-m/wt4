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

package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.google.common.base.Objects;

import java.net.URI;
import java.util.Map;

/**
 * Represents project allowed to choose to create new issue. Also contains issue types allowed for that project described by {@link CimIssueType} class.<br/>
 * The CIM prefix stands for CreateIssueMetadata as this class is used in output of {@link IssueRestClient#getCreateIssueMetadata(GetCreateIssueMetadataOptions, ProgressMonitor)}
 *
 * @since v1.0
 */
public class CimProject extends BasicProject {

	private final Map<String, URI> avatarUris;
	private final Iterable<CimIssueType> issueTypes;

	public CimProject(URI self, String key, Long id, String name, Map<String, URI> avatarUris, Iterable<CimIssueType> issueTypes) {
		super(self, key, id, name);
		this.avatarUris = avatarUris;
		this.issueTypes = issueTypes;
	}

	public Iterable<CimIssueType> getIssueTypes() {
		return issueTypes;
	}

	public Map<String, URI> getAvatarUris() {
		return avatarUris;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Objects.ToStringHelper getToStringHelper() {
		return super.getToStringHelper().
				add("issueTypes", issueTypes).
				add("avatarUris", avatarUris);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), avatarUris, issueTypes);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CimProject) {
			CimProject that = (CimProject) obj;
			return super.equals(obj)
					&& Objects.equal(this.avatarUris, that.avatarUris)
					&& Objects.equal(this.issueTypes, that.issueTypes);
		}
		return false;
	}
}
