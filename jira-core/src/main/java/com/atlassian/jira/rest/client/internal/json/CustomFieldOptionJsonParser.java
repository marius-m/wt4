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

import com.atlassian.jira.rest.client.api.domain.CustomFieldOption;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.Collections;

/**
 * JSON parser for CustomFieldOption
 *
 * @since v1.0
 */
public class CustomFieldOptionJsonParser implements JsonObjectParser<CustomFieldOption> {

	private final JsonArrayParser<Iterable<CustomFieldOption>> childrenParser = GenericJsonArrayParser.create(this);

	@Override
	public CustomFieldOption parse(JSONObject json) throws JSONException {
		final URI selfUri = JsonParseUtil.getSelfUri(json);
		final long id = json.getLong("id");
		final String value = json.getString("value");

		final JSONArray childrenArray = json.optJSONArray("children");
		final Iterable<CustomFieldOption> children = (childrenArray != null)
				? childrenParser.parse(childrenArray)
				: Collections.<CustomFieldOption>emptyList();

		final JSONObject childObject = json.optJSONObject("child");
		final CustomFieldOption child = (childObject != null) ? parse(childObject) : null;

		return new CustomFieldOption(id, selfUri, value, children, child);
	}
}
