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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Filter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

/**
 * JSON parser for Filter.
 *
 * @since v2.0
 */
public class FilterJsonParser implements JsonObjectParser<Filter> {

	@Override
	public Filter parse(JSONObject json) throws JSONException {
		final URI selfUri = JsonParseUtil.getSelfUri(json);
		final long id = json.getLong("id");
		final String name = json.getString("name");
		final String jql = json.getString("jql");
		final String description = json.optString("description");
		final URI searchUrl = JsonParseUtil.parseURI(json.getString("searchUrl"));
		final URI viewUrl = JsonParseUtil.parseURI(json.getString("viewUrl"));
		final BasicUser owner = JsonParseUtil.parseBasicUser(json.getJSONObject("owner"));
		final boolean favourite = json.getBoolean("favourite");
		return new Filter(selfUri, id, name, description, jql, viewUrl, searchUrl, owner, favourite);
	}
}
