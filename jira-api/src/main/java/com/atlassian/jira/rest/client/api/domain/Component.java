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

import javax.annotation.Nullable;
import java.net.URI;

/**
 * Project component
 *
 * @since v0.1
 */
public class Component extends BasicComponent {
	private final BasicUser lead;

	@Nullable
	private AssigneeInfo assigneeInfo;


	public Component(URI self, @Nullable Long id, String name, String description, BasicUser lead) {
		super(self, id, name, description);
		this.lead = lead;
	}

	public Component(URI self, @Nullable Long id, String name, String description, BasicUser lead, @Nullable AssigneeInfo assigneeInfo) {
		this(self, id, name, description, lead);
		this.assigneeInfo = assigneeInfo;
	}

	public BasicUser getLead() {
		return lead;
	}

	/**
	 * @return detailed info about auto-assignee for this project component or <code>null</code> if such information is
	 *         not available (JIRA prior 4.4)
	 * @since com.atlassian.jira.rest.client.api 0.3, server 4.4
	 */
	@Nullable
	public AssigneeInfo getAssigneeInfo() {
		return assigneeInfo;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(super.toString()).
				add("lead", lead).
				add("assigneeInfo", assigneeInfo).
				toString();
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Component) {
			Component that = (Component) obj;
			return super.equals(obj) && Objects.equal(this.lead, that.lead)
					&& Objects.equal(this.assigneeInfo, that.assigneeInfo);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), lead, assigneeInfo);
	}

	public static class AssigneeInfo {
		@Nullable
		private final BasicUser assignee;
		private final AssigneeType assigneeType;
		@Nullable
		private final BasicUser realAssignee;
		private final AssigneeType realAssigneeType;
		private final boolean isAssigneeTypeValid;

		public AssigneeInfo(BasicUser assignee, AssigneeType assigneeType, @Nullable BasicUser realAssignee, AssigneeType realAssigneeType, boolean assigneeTypeValid) {
			this.assignee = assignee;
			this.assigneeType = assigneeType;
			this.realAssignee = realAssignee;
			this.realAssigneeType = realAssigneeType;
			isAssigneeTypeValid = assigneeTypeValid;
		}

		@Nullable
		public BasicUser getAssignee() {
			return assignee;
		}

		public AssigneeType getAssigneeType() {
			return assigneeType;
		}

		@Nullable
		public BasicUser getRealAssignee() {
			return realAssignee;
		}

		public AssigneeType getRealAssigneeType() {
			return realAssigneeType;
		}

		public boolean isAssigneeTypeValid() {
			return isAssigneeTypeValid;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).
					add("assignee", assignee).
					add("assigneeType", assigneeType).
					add("realAssignee", realAssignee).
					add("realAssigneeType", realAssigneeType).
					add("isAssigneeTypeValid", isAssigneeTypeValid).
					toString();
		}


		@Override
		public boolean equals(Object obj) {
			if (obj instanceof AssigneeInfo) {
				AssigneeInfo that = (AssigneeInfo) obj;
				return Objects.equal(this.assignee, that.assignee)
						&& Objects.equal(this.assigneeType, that.assigneeType)
						&& Objects.equal(this.realAssignee, that.realAssignee)
						&& Objects.equal(this.realAssigneeType, that.realAssigneeType)
						&& Objects.equal(this.isAssigneeTypeValid, that.isAssigneeTypeValid);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(super
					.hashCode(), assignee, assigneeType, realAssignee, realAssigneeType, isAssigneeTypeValid);
		}

	}
}
