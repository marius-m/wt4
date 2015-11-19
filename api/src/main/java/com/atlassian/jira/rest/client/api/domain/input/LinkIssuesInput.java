/*
 * Copyright (C) 2011 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.api.domain.input;

import com.atlassian.jira.rest.client.api.domain.Comment;

import javax.annotation.Nullable;

/**
 * Input parameters used for issue link creation.
 */
public class LinkIssuesInput {
	private final String fromIssueKey;
	private final String toIssueKey;
	private final String linkType;
	private final Comment comment;

	/**
	 * @param fromIssueKey source issue key
	 * @param toIssueKey   destination issue key
	 * @param linkType     name of the link type (e.g. "Duplicate")
	 * @param comment      optional comment
	 */
	public LinkIssuesInput(String fromIssueKey, String toIssueKey, String linkType, @Nullable Comment comment) {
		this.fromIssueKey = fromIssueKey;
		this.toIssueKey = toIssueKey;
		this.comment = comment;
		this.linkType = linkType;
	}

	public LinkIssuesInput(String fromIssueKey, String toIssueKey, String linkType) {
		this(fromIssueKey, toIssueKey, linkType, null);
	}


	/**
	 * @return source issue key
	 */
	public String getFromIssueKey() {
		return fromIssueKey;
	}

	/**
	 * @return destination issue key
	 */
	public String getToIssueKey() {
		return toIssueKey;
	}

	/**
	 * @return optional comment or <code>null</code>
	 */
	@Nullable
	public Comment getComment() {
		return comment;
	}

	/**
	 * @return name of the link type (e.g. "Duplicate")
	 */
	public String getLinkType() {
		return linkType;
	}
}
