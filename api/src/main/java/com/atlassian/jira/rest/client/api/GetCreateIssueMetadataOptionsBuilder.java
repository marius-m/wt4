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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Builder class for {@link GetCreateIssueMetadataOptions}. All fields are optional so set only those that
 * you need and use {@link GetCreateIssueMetadataOptionsBuilder#build()} method to build new
 * {@link GetCreateIssueMetadataOptions} class.<br/>
 * <strong>Please note</strong> that all setters for fields sets given value instead of adding it.it
 * to previously set. For example doing:<br/>
 * <code>new GetCreateIssueMetadataOptionsBuilder().withExpandos("ONE", "TWO").withExpandos("THREE").build()</code><br/>
 * will result in creating new GetCreateIssueMetadataOptions with only one field marked to be expanded - "THREE"
 *
 * @since v1.0
 */
public class GetCreateIssueMetadataOptionsBuilder {
	private Iterable<String> expandos = Sets.newHashSet();
	private Iterable<String> issueTypeNames;
	private Iterable<Long> issueTypeIds;
	private Iterable<String> projectKeys;
	private Iterable<Long> projectIds;

	public GetCreateIssueMetadataOptionsBuilder withExpandos(Iterable<String> expandos) {
		this.expandos = expandos;
		return this;
	}

	@SuppressWarnings("UnusedDeclaration")
	public GetCreateIssueMetadataOptionsBuilder withExpandos(String... expandos) {
		return withExpandos(ImmutableList.copyOf(expandos));
	}

	public GetCreateIssueMetadataOptionsBuilder withExpandedIssueTypesFields() {
		return withExpandos(ImmutableList.of(GetCreateIssueMetadataOptions.EXPAND_PROJECTS_ISSUETYPES_FIELDS));
	}

	public GetCreateIssueMetadataOptionsBuilder withIssueTypeNames(Iterable<String> issueTypeNames) {
		this.issueTypeNames = issueTypeNames;
		return this;
	}

	@SuppressWarnings("UnusedDeclaration")

	public GetCreateIssueMetadataOptionsBuilder withIssueTypeNames(String... issueTypeNames) {
		return withIssueTypeNames(ImmutableList.copyOf(issueTypeNames));
	}

	public GetCreateIssueMetadataOptionsBuilder withIssueTypeIds(Iterable<Long> issueTypeIds) {
		this.issueTypeIds = issueTypeIds;
		return this;
	}

	@SuppressWarnings("UnusedDeclaration")
	public GetCreateIssueMetadataOptionsBuilder withIssueTypeIds(Long... issueTypeIds) {
		return withIssueTypeIds(ImmutableList.copyOf(issueTypeIds));
	}

	public GetCreateIssueMetadataOptionsBuilder withProjectKeys(Iterable<String> projectKeys) {
		this.projectKeys = projectKeys;
		return this;
	}

	public GetCreateIssueMetadataOptionsBuilder withProjectKeys(String... projectKeys) {
		return withProjectKeys(ImmutableList.copyOf(projectKeys));
	}

	public GetCreateIssueMetadataOptionsBuilder withProjectIds(Iterable<Long> projectIds) {
		this.projectIds = projectIds;
		return this;
	}

	@SuppressWarnings("UnusedDeclaration")
	public GetCreateIssueMetadataOptionsBuilder withProjectIds(Long... projectIds) {
		return withProjectIds(ImmutableList.copyOf(projectIds));
	}

	public GetCreateIssueMetadataOptions build() {
		return new GetCreateIssueMetadataOptions(expandos, issueTypeNames, issueTypeIds, projectKeys, projectIds);
	}
}