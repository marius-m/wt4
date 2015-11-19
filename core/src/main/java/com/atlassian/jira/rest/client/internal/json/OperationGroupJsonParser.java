/*
 * Copyright (C) 2014 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.OperationGroup;
import com.atlassian.jira.rest.client.api.domain.OperationHeader;
import com.atlassian.jira.rest.client.api.domain.OperationLink;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class OperationGroupJsonParser implements JsonObjectParser<OperationGroup> {
	final private OperationLinkJsonParser linkJsonParser = new OperationLinkJsonParser();
	final private OperationHeaderJsonParser headerJsonParser = new OperationHeaderJsonParser();

	@Override
	public OperationGroup parse(final JSONObject json) throws JSONException {
		final String id = JsonParseUtil.getOptionalString(json, "id");
		final Iterable<OperationLink> links = JsonParseUtil.parseJsonArray(json.getJSONArray("links"), linkJsonParser);
		final Iterable<OperationGroup> groups = JsonParseUtil.parseJsonArray(json.getJSONArray("groups"), this);
		final OperationHeader header = JsonParseUtil.parseOptionalJsonObject(json, "header", headerJsonParser);
		final Integer weight = JsonParseUtil.parseOptionInteger(json, "weight");
		return new OperationGroup(id, links, groups, header, weight);
	}
}
