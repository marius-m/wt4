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

import javax.annotation.Nullable;

/**
 * Set of optional parameters for {@link IssueRestClient#getCreateIssueMetadata(GetCreateIssueMetadataOptions)}.
 * {@link GetCreateIssueMetadataOptionsBuilder} is very useful for building objects of this class.
 *
 * @since v1.0
 */
public class GetCreateIssueMetadataOptions {

	public static final String EXPAND_PROJECTS_ISSUETYPES_FIELDS = "projects.issuetypes.fields";

	@Nullable
	public final Iterable<Long> projectIds;
	@Nullable
	public final Iterable<String> projectKeys;
	@Nullable
	public final Iterable<Long> issueTypeIds;
	@Nullable
	public final Iterable<String> issueTypeNames;
	@Nullable
	public final Iterable<String> expandos;

	/**
	 * @param expandos       List of fields that should be expanded. See constants with prefix EXPAND_ in this class. Pass <code>null</code> to ignore.
	 * @param issueTypeNames List of issue types names to filter results. Pass <code>null</code> to ignore.
	 * @param issueTypeIds   List of issue types Ids to filter results. Pass <code>null</code> to ignore.
	 * @param projectKeys    List of projects keys used to filter results. Pass <code>null</code> to ignore.
	 * @param projectIds     List of projects Ids used to filter results. Pass <code>null</code> to ignore.
	 */
	public GetCreateIssueMetadataOptions(@Nullable Iterable<String> expandos, @Nullable Iterable<String> issueTypeNames,
			@Nullable Iterable<Long> issueTypeIds, @Nullable Iterable<String> projectKeys,
			@Nullable Iterable<Long> projectIds) {
		this.expandos = expandos;
		this.issueTypeNames = issueTypeNames;
		this.issueTypeIds = issueTypeIds;
		this.projectKeys = projectKeys;
		this.projectIds = projectIds;
	}
}
