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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Keeps field id that may be used to refer to field in fields maps.
 */
public enum IssueFieldId {
	AFFECTS_VERSIONS_FIELD("versions"),
	ASSIGNEE_FIELD("assignee"),
	ATTACHMENT_FIELD("attachment"),
	COMMENT_FIELD("comment"),
	COMPONENTS_FIELD("components"),
	CREATED_FIELD("created"),
	DESCRIPTION_FIELD("description"),
	DUE_DATE_FIELD("duedate"),
	FIX_VERSIONS_FIELD("fixVersions"),
	ISSUE_TYPE_FIELD("issuetype"),
	LABELS_FIELD("labels"),
	LINKS_FIELD("issuelinks"),
	LINKS_PRE_5_0_FIELD("links"),
	PRIORITY_FIELD("priority"),
	PROJECT_FIELD("project"),
	REPORTER_FIELD("reporter"),
	RESOLUTION_FIELD("resolution"),
	STATUS_FIELD("status"),
	SUBTASKS_FIELD("subtasks"),
	SUMMARY_FIELD("summary"),
	TIMETRACKING_FIELD("timetracking"),
	TRANSITIONS_FIELD("transitions"),
	UPDATED_FIELD("updated"),
	VOTES_FIELD("votes"),
	WATCHER_FIELD("watches"),
	WATCHER_PRE_5_0_FIELD("watcher"),
	WORKLOG_FIELD("worklog"),
	WORKLOGS_FIELD("worklogs");

	public final String id;

	IssueFieldId(String id) {
		this.id = id;
	}

	public static final Function<IssueFieldId, String> TRANSFORM_TO_ID_FUNCTION = new Function<IssueFieldId, String>() {
		@Override
		public String apply(IssueFieldId from) {
			return from.id;
		}
	};

	/**
	 * Returns all fields ids.
	 *
	 * @return List of string id of each field.
	 */
	public static Iterable<String> ids() {
		return Iterables.transform(Lists.newArrayList(IssueFieldId.values()), IssueFieldId.TRANSFORM_TO_ID_FUNCTION);
	}
}
