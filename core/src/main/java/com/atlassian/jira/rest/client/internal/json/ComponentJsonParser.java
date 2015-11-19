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

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.AssigneeType;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.internal.domain.AssigneeTypeConstants;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ComponentJsonParser implements JsonObjectParser<Component> {
	@Override
	public Component parse(JSONObject json) throws JSONException {
		final BasicComponent basicComponent = BasicComponentJsonParser.parseBasicComponent(json);
		final JSONObject leadJson = json.optJSONObject("lead");
		final BasicUser lead = leadJson != null ? JsonParseUtil.parseBasicUser(leadJson) : null;
		final String assigneeTypeStr = JsonParseUtil.getOptionalString(json, "assigneeType");
		final Component.AssigneeInfo assigneeInfo;
		if (assigneeTypeStr != null) {
			final AssigneeType assigneeType = parseAssigneeType(assigneeTypeStr);
			final JSONObject assigneeJson = json.optJSONObject("assignee");
			final BasicUser assignee = assigneeJson != null ? JsonParseUtil.parseBasicUser(assigneeJson) : null;
			final AssigneeType realAssigneeType = parseAssigneeType(json.getString("realAssigneeType"));
			final JSONObject realAssigneeJson = json.optJSONObject("realAssignee");
			final BasicUser realAssignee = realAssigneeJson != null ? JsonParseUtil.parseBasicUser(realAssigneeJson) : null;
			final boolean isAssigneeTypeValid = json.getBoolean("isAssigneeTypeValid");
			assigneeInfo = new Component.AssigneeInfo(assignee, assigneeType, realAssignee, realAssigneeType, isAssigneeTypeValid);
		} else {
			assigneeInfo = null;
		}

		return new Component(basicComponent.getSelf(), basicComponent.getId(), basicComponent.getName(), basicComponent
				.getDescription(), lead, assigneeInfo);
	}

	AssigneeType parseAssigneeType(String str) throws JSONException {
		// JIRA 4.4+ adds full assignee info to component resource
		if (AssigneeTypeConstants.COMPONENT_LEAD.equals(str)) {
			return AssigneeType.COMPONENT_LEAD;
		}
		if (AssigneeTypeConstants.PROJECT_DEFAULT.equals(str)) {
			return AssigneeType.PROJECT_DEFAULT;
		}
		if (AssigneeTypeConstants.PROJECT_LEAD.equals(str)) {
			return AssigneeType.PROJECT_LEAD;
		}
		if (AssigneeTypeConstants.UNASSIGNED.equals(str)) {
			return AssigneeType.UNASSIGNED;
		}
		throw new JSONException("Unexpected value of assignee type [" + str + "]");
	}
}
