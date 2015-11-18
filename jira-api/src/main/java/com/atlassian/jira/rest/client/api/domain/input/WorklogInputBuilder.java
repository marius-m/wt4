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

package com.atlassian.jira.rest.client.api.domain.input;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.google.common.base.Preconditions;
import org.joda.time.DateTime;

import java.net.URI;

/**
 * Builder class for WorklogInput. Allows to create new worklogInput instance by using convenient setters.
 * Especially useful are methods to set estimate adjustment options:
 * {@link WorklogInputBuilder#setAdjustEstimateAuto()}, {@link WorklogInputBuilder#setAdjustEstimateLeave()},
 * {@link WorklogInputBuilder#setAdjustEstimateManual(String)} and {@link WorklogInputBuilder#setAdjustEstimateNew(String)}.
 * <br/>
 * If you want ot create new WorklogInput from existing Worklog entity then use
 * {@link WorklogInputBuilder#copyFromWorklog(com.atlassian.jira.rest.client.api.domain.Worklog)} method.
 */
public class WorklogInputBuilder {
	public static final String ESTIMATE_UNIT_MINUTES = "m";
	private URI self;
	private URI issueUri;
	private BasicUser author;
	private BasicUser updateAuthor;
	private String comment;
	private DateTime startDate;
	private int minutesSpent;
	private Visibility visibility;
	private WorklogInput.AdjustEstimate adjustEstimate = WorklogInput.AdjustEstimate.AUTO;
	private String adjustEstimateValue;

	public WorklogInputBuilder(URI issueUri) {
		Preconditions.checkNotNull(issueUri, "The issueUri cannot be null");
		this.issueUri = issueUri;
	}

	@SuppressWarnings("UnusedDeclaration")
	public WorklogInputBuilder copyFromWorklog(Worklog worklog) {
		return this
				.setSelf(worklog.getSelf())
				.setIssueUri(worklog.getIssueUri())
				.setAuthor(worklog.getAuthor())
				.setUpdateAuthor(worklog.getUpdateAuthor())
				.setComment(worklog.getComment())
				.setStartDate(worklog.getStartDate())
				.setMinutesSpent(worklog.getMinutesSpent())
				.setVisibility(worklog.getVisibility());
	}


	private WorklogInputBuilder setAdjustEstimate(WorklogInput.AdjustEstimate adjustEstimate, String estimateValue) {
		this.adjustEstimate = adjustEstimate;
		this.adjustEstimateValue = estimateValue;
		return this;
	}

	/**
	 * Sets AdjustEstimate to NEW - sets estimate to specified value.
	 *
	 * @param newEstimate new estimate value to set.<br/>
	 *                    You can specify a time unit after a time value 'X', such as Xw, Xd, Xh or Xm,
	 *                    to represent weeks (w), days (d), hours (h) and minutes (m), respectively.<br/>
	 *                    If you do not specify a time unit, minute will be assumed.
	 * @return this worklog input builder object
	 */
	public WorklogInputBuilder setAdjustEstimateNew(String newEstimate) {
		return setAdjustEstimate(WorklogInput.AdjustEstimate.NEW, newEstimate);
	}

	/**
	 * Sets AdjustEstimate to NEW - sets estimate to specified value.
	 *
	 * @param newEstimateMinutes new estimate value to set, in minutes.
	 * @return this worklog input builder object
	 */
	public WorklogInputBuilder setAdjustEstimateNew(int newEstimateMinutes) {
		return setAdjustEstimate(WorklogInput.AdjustEstimate.NEW, newEstimateMinutes + ESTIMATE_UNIT_MINUTES);
	}

	/**
	 * Sets AdjustEstimate to LEAVE - leaves estimate as is.
	 *
	 * @return this worklog input builder object
	 */
	public WorklogInputBuilder setAdjustEstimateLeave() {
		return setAdjustEstimate(WorklogInput.AdjustEstimate.LEAVE, null);
	}

	/**
	 * Sets AdjustEstimate to MANUAL - reduces remaining estimate by given value.
	 *
	 * @param reduceEstimateBy the amount to reduce the remaining estimate by<br/>
	 *                         You can specify a time unit after a time value 'X', such as Xw, Xd, Xh or Xm,
	 *                         to represent weeks (w), days (d), hours (h) and minutes (m), respectively.<br/>
	 *                         If you do not specify a time unit, minute will be assumed.
	 * @return this worklog input builder object
	 */
	public WorklogInputBuilder setAdjustEstimateManual(String reduceEstimateBy) {
		return setAdjustEstimate(WorklogInput.AdjustEstimate.MANUAL, reduceEstimateBy);
	}

	/**
	 * Sets AdjustEstimate to MANUAL - reduces remaining estimate by given value.
	 *
	 * @param reduceEstimateByMinutes the amount to reduce the remaining estimate by, in minutes.
	 * @return this worklog input builder object
	 */
	public WorklogInputBuilder setAdjustEstimateManual(int reduceEstimateByMinutes) {
		return setAdjustEstimate(WorklogInput.AdjustEstimate.MANUAL, reduceEstimateByMinutes + ESTIMATE_UNIT_MINUTES);
	}

	/**
	 * Sets AdjustEstimate to AUTO - will automatically adjust the value
	 * based on the minutes spend specified on the worklog input.<br/>
	 * This is the default option.
	 *
	 * @return this worklog input builder object
	 */
	@SuppressWarnings("UnusedDeclaration")
	public WorklogInputBuilder setAdjustEstimateAuto() {
		return setAdjustEstimate(WorklogInput.AdjustEstimate.AUTO, null);
	}

	public WorklogInputBuilder setSelf(URI self) {
		this.self = self;
		return this;
	}

	public WorklogInputBuilder setIssueUri(URI issueUri) {
		this.issueUri = issueUri;
		return this;
	}

	public WorklogInputBuilder setAuthor(BasicUser author) {
		this.author = author;
		return this;
	}

	public WorklogInputBuilder setUpdateAuthor(BasicUser updateAuthor) {
		this.updateAuthor = updateAuthor;
		return this;
	}

	public WorklogInputBuilder setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public WorklogInputBuilder setStartDate(DateTime startDate) {
		this.startDate = startDate;
		return this;
	}

	public WorklogInputBuilder setMinutesSpent(int minutesSpent) {
		this.minutesSpent = minutesSpent;
		return this;
	}

	public WorklogInputBuilder setVisibility(Visibility visibility) {
		this.visibility = visibility;
		return this;
	}

	public WorklogInput build() {
		return new WorklogInput(self, issueUri, author, updateAuthor, comment, startDate, minutesSpent, visibility, adjustEstimate, adjustEstimateValue);
	}
}