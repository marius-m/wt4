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

import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.FieldType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ChangelogItemJsonParser implements JsonObjectParser<ChangelogItem> {
	@Override
	public ChangelogItem parse(JSONObject json) throws JSONException {
		final String fieldTypeStr = JsonParseUtil.getNestedString(json, "fieldtype");
		final FieldType fieldType;
		if ("jira".equalsIgnoreCase(fieldTypeStr)) {
			fieldType = FieldType.JIRA;
		} else if ("custom".equalsIgnoreCase(fieldTypeStr)) {
			fieldType = FieldType.CUSTOM;
		} else {
			throw new JSONException("[" + fieldTypeStr + "] does not represent a valid field type. Expected [jira] or [custom].");
		}
		final String field = JsonParseUtil.getNestedString(json, "field");
		final String from = JsonParseUtil.getNullableString(json, "from");
		final String fromString = JsonParseUtil.getNullableString(json, "fromString");
		final String to = JsonParseUtil.getNullableString(json, "to");
		final String toString = JsonParseUtil.getNullableString(json, "toString");
		return new ChangelogItem(fieldType, field, from, fromString, to, toString);
	}
}
