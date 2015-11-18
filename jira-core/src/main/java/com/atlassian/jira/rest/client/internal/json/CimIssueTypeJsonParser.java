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

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Collections;
import java.util.Map;

/**
 * JSON parser for CimIssueType
 *
 * @since v1.0
 */
public class CimIssueTypeJsonParser implements JsonObjectParser<CimIssueType> {

	final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
	final CimFieldsInfoMapJsonParser fieldsParser = new CimFieldsInfoMapJsonParser();

	@Override
	public CimIssueType parse(final JSONObject json) throws JSONException {
		final IssueType issueType = issueTypeJsonParser.parse(json);
		final JSONObject jsonFieldsMap = json.optJSONObject("fields");

		final Map<String, CimFieldInfo> fields = (jsonFieldsMap == null) ?
				Collections.<String, CimFieldInfo>emptyMap() : fieldsParser.parse(jsonFieldsMap);

		return new CimIssueType(issueType.getSelf(), issueType.getId(), issueType.getName(),
				issueType.isSubtask(), issueType.getDescription(), issueType.getIconUri(), fields);
	}
}
