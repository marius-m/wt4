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

import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.Priority;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

public class PriorityJsonParser implements JsonObjectParser<Priority> {
	private final BasicPriorityJsonParser basicPriorityJsonParser = new BasicPriorityJsonParser();

	@Override
	public Priority parse(JSONObject json) throws JSONException {
		final BasicPriority basicPriority = basicPriorityJsonParser.parse(json);
		final String statusColor = json.getString("statusColor");
		final String description = json.getString("description");
		final URI iconUri = JsonParseUtil.parseURI(json.getString("iconUrl"));
		return new Priority(basicPriority.getSelf(), basicPriority.getId(), basicPriority
				.getName(), statusColor, description, iconUri);
	}
}
