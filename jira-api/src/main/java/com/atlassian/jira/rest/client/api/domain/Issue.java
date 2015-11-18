/*
 * Copyright (C) 2010-2014 Atlassian
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

import com.atlassian.jira.rest.client.api.ExpandableResource;
import com.atlassian.jira.rest.client.api.domain.util.UriUtil;
import com.google.common.base.Objects;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Collection;
import java.util.Set;

import static com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;

/**
 * Single JIRA issue
 *
 * @since v0.1
 */
public class Issue extends BasicIssue implements ExpandableResource {

	public Issue(String summary, URI self, String key, Long id, BasicProject project, IssueType issueType, Status status,
			String description, @Nullable BasicPriority priority, @Nullable Resolution resolution, Collection<Attachment> attachments,
			@Nullable User reporter, @Nullable User assignee, DateTime creationDate, DateTime updateDate, DateTime dueDate,
			Collection<Version> affectedVersions, Collection<Version> fixVersions, Collection<BasicComponent> components,
			@Nullable TimeTracking timeTracking, Collection<IssueField> issueFields, Collection<Comment> comments,
			@Nullable URI transitionsUri,
			@Nullable Collection<IssueLink> issueLinks,
			BasicVotes votes, Collection<Worklog> worklogs, BasicWatchers watchers, Iterable<String> expandos,
			@Nullable Collection<Subtask> subtasks, @Nullable Collection<ChangelogGroup> changelog, @Nullable Operations operations,
			Set<String> labels) {
		super(self, key, id);
		this.summary = summary;
		this.project = project;
		this.status = status;
		this.description = description;
		this.resolution = resolution;
		this.expandos = expandos;
		this.comments = comments;
		this.attachments = attachments;
		this.issueFields = issueFields;
		this.issueType = issueType;
		this.reporter = reporter;
		this.assignee = assignee;
		this.creationDate = creationDate;
		this.updateDate = updateDate;
		this.dueDate = dueDate;
		this.transitionsUri = transitionsUri;
		this.issueLinks = issueLinks;
		this.votes = votes;
		this.worklogs = worklogs;
		this.watchers = watchers;
		this.fixVersions = fixVersions;
		this.affectedVersions = affectedVersions;
		this.components = components;
		this.priority = priority;
		this.timeTracking = timeTracking;
		this.subtasks = subtasks;
		this.changelog = changelog;
		this.operations = operations;
		this.labels = labels;
	}

	private final Status status;
	private final IssueType issueType;
	private final BasicProject project;
	private final URI transitionsUri;
	private final Iterable<String> expandos;
	private final Collection<BasicComponent> components;
	private final String summary;
	@Nullable
	private final String description;
	@Nullable
	private final User reporter;
	private final User assignee;
	@Nullable
	private final Resolution resolution;
	private final Collection<IssueField> issueFields;
	private final DateTime creationDate;
	private final DateTime updateDate;
	private final DateTime dueDate;
	private final BasicPriority priority;
	private final BasicVotes votes;
	@Nullable
	private final Collection<Version> fixVersions;
	@Nullable
	private final Collection<Version> affectedVersions;

	private final Collection<Comment> comments;

	@Nullable
	private final Collection<IssueLink> issueLinks;

	private final Collection<Attachment> attachments;

	private final Collection<Worklog> worklogs;
	private final BasicWatchers watchers;

	@Nullable
	private final TimeTracking timeTracking;
	@Nullable
	private final Collection<Subtask> subtasks;
	@Nullable
	private final Collection<ChangelogGroup> changelog;
	@Nullable
	private final Operations operations;
	private final Set<String> labels;

	public Status getStatus() {
		return status;
	}

	/**
	 * @return reporter of this issue or <code>null</code> if this issue has no reporter
	 */
	@Nullable
	public User getReporter() {
		return reporter;
	}

	/**
	 * @return assignee of this issue or <code>null</code> if this issue is unassigned.
	 */
	@Nullable
	public User getAssignee() {
		return assignee;
	}


	public String getSummary() {
		return summary;
	}

	/**
	 * @return priority of this issue
	 */
	@Nullable
	public BasicPriority getPriority() {
		return priority;
	}

	/**
	 * @return issue links for this issue (possibly nothing) or <code>null</code> when issue links are deactivated for this JIRA instance
	 */
	@Nullable
	public Iterable<IssueLink> getIssueLinks() {
		return issueLinks;
	}

	@Nullable
	public Iterable<Subtask> getSubtasks() {
		return subtasks;
	}

	/**
	 * @return fields inaccessible by concrete getter methods (e.g. all custom issueFields)
	 */
	public Iterable<IssueField> getFields() {
		return issueFields;
	}

	/**
	 * @param id identifier of the field (inaccessible by concrete getter method)
	 * @return field with given id, or <code>null</code> when no field with given id exists for this issue
	 */
	@Nullable
	public IssueField getField(String id) {
		for (IssueField issueField : issueFields) {
			if (issueField.getId().equals(id)) {
				return issueField;
			}
		}
		return null;
	}

	/**
	 * This method returns the first field with specified name.
	 * Names of fields in JIRA do not need to be unique. Therefore this method does not guarantee that you will get what you really want.
	 * It's added just for convenience. For identify fields you should use id rather than name.
	 *
	 * @param name name of the field.
	 * @return the first field matching selected name or <code>null</code> when no field with given name exists for this issue
	 */
	@Nullable
	public IssueField getFieldByName(String name) {
		for (IssueField issueField : issueFields) {
			if (issueField.getName().equals(name)) {
				return issueField;
			}
		}
		return null;
	}

	@Override
	public Iterable<String> getExpandos() {
		return expandos;
	}

	/**
	 * @return issue type
	 */
	public IssueType getIssueType() {
		return issueType;
	}

	/**
	 * @return attachments of this issue
	 */
	public Iterable<Attachment> getAttachments() {
		return attachments;
	}

	public URI getAttachmentsUri() {
		return UriUtil.path(getSelf(), "attachments");
	}

	public URI getWorklogUri() {
		return UriUtil.path(getSelf(), "worklog");
	}

	/**
	 * @return comments for this issue
	 */
	public Iterable<Comment> getComments() {
		return comments;
	}

	public URI getCommentsUri() {
		return UriUtil.path(getSelf(), "comment");
	}

	/**
	 * @return project this issue belongs to
	 */
	public BasicProject getProject() {
		return project;
	}

	/**
	 * @return <code>null</code when voting is disabled in JIRA
	 */
	@Nullable
	public BasicVotes getVotes() {
		return votes;
	}

	public Iterable<Worklog> getWorklogs() {
		return worklogs;
	}

	/**
	 * @return <code>null</code> when watching is disabled in JIRA
	 */
	@Nullable
	public BasicWatchers getWatchers() {
		return watchers;
	}

	@Nullable
	public Iterable<Version> getFixVersions() {
		return fixVersions;
	}

	@Nullable
	public URI getTransitionsUri() {
		return transitionsUri;
	}

	@Nullable
	public Iterable<Version> getAffectedVersions() {
		return affectedVersions;
	}

	public Iterable<BasicComponent> getComponents() {
		return components;
	}

	public Set<String> getLabels() {
		return labels;
	}

	/**
	 * Returns changelog available for issues retrieved with CHANGELOG expanded.
	 *
	 * @return issue changelog or <code>null</code> if CHANGELOG has not been expanded or REST API on the server side does not serve
	 *         this information (pre-5.0)
	 * @see com.atlassian.jira.rest.client.api.IssueRestClient#getIssue(String, Iterable)
	 * @since com.atlassian.jira.rest.client.api 0.6, server 5.0
	 */
	@Nullable
	public Iterable<ChangelogGroup> getChangelog() {
		return changelog;
	}

	/**
	 * Returns operations available/allowed for issues retrieved with {@link Expandos#OPERATIONS} expanded.
	 *
	 * @return issue operations or <code>null</code> if {@link Expandos#OPERATIONS} has not been expanded or
	 * REST API on the server side does not serve this information (pre-5.0)
	 * @see com.atlassian.jira.rest.client.api.IssueRestClient#getIssue(String, Iterable)
	 * @since com.atlassian.jira.rest.client.api 2.0, server 5.0
	 */
	@Nullable
	public Operations getOperations() {
		return operations;
	}

	public URI getVotesUri() {
		return UriUtil.path(getSelf(), "votes");
	}


	@Nullable
	public Resolution getResolution() {
		return resolution;
	}

	public DateTime getCreationDate() {
		return creationDate;
	}

	public DateTime getUpdateDate() {
		return updateDate;
	}

	public DateTime getDueDate() {
		return dueDate;
	}

	@Nullable
	public TimeTracking getTimeTracking() {
		return timeTracking;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Override
	protected Objects.ToStringHelper getToStringHelper() {
		return super.getToStringHelper().
				add("project", project).
				add("status", status).
				add("description", description).
				add("expandos", expandos).
				add("resolution", resolution).
				add("reporter", reporter).
				add("assignee", assignee).addValue("\n").
				add("fields", issueFields).addValue("\n").
				add("affectedVersions", affectedVersions).addValue("\n").
				add("fixVersions", fixVersions).addValue("\n").
				add("components", components).addValue("\n").
				add("issueType", issueType).
				add("creationDate", creationDate).
				add("updateDate", updateDate).addValue("\n").
				add("dueDate", dueDate).addValue("\n").
				add("attachments", attachments).addValue("\n").
				add("comments", comments).addValue("\n").
				add("transitionsUri", transitionsUri).
				add("issueLinks", issueLinks).addValue("\n").
				add("votes", votes).addValue("\n").
				add("worklogs", worklogs).addValue("\n").
				add("watchers", watchers).
				add("timeTracking", timeTracking).
				add("changelog", changelog).
				add("operations", operations).
				add("labels", labels);
	}
}
