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

import com.atlassian.jira.rest.client.api.domain.FieldSchema;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * JSON parser for FieldSchema
 *
 * @since v1.0
 */
public class FieldSchemaJsonParser implements JsonObjectParser<FieldSchema> {

	@Override
	public FieldSchema parse(JSONObject json) throws JSONException {
		final String type = JsonParseUtil.getOptionalString(json, "type");
		final String items = JsonParseUtil.getOptionalString(json, "items");
		final String system = JsonParseUtil.getOptionalString(json, "system");
		final String custom = JsonParseUtil.getOptionalString(json, "custom");
		final Long customId = JsonParseUtil.getOptionalLong(json, "customId");

		return new FieldSchema(type, items, system, custom, customId);
	}
}
