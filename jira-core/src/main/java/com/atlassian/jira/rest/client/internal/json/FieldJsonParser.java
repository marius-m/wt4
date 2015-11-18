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

import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.FieldSchema;
import com.atlassian.jira.rest.client.api.domain.FieldType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * JSON parser for JIRA fields.
 */
public class FieldJsonParser implements JsonObjectParser<Field> {

	private final FieldSchemaJsonParser schemaJsonParser = new FieldSchemaJsonParser();

	@Override
	public Field parse(final JSONObject jsonObject) throws JSONException {
		final String id = jsonObject.getString("id");
		final String name = jsonObject.getString("name");
		final Boolean orderable = jsonObject.getBoolean("orderable");
		final Boolean navigable = jsonObject.getBoolean("navigable");
		final Boolean searchable = jsonObject.getBoolean("searchable");
		final FieldType custom = jsonObject.getBoolean("custom") ? FieldType.CUSTOM : FieldType.JIRA;
		final FieldSchema schema = jsonObject.has("schema") ? schemaJsonParser.parse(jsonObject.getJSONObject("schema")) : null;
		return new Field(id, name, custom, orderable, navigable, searchable, schema);
	}

	public static JsonArrayParser<Iterable<Field>> createFieldsArrayParser() {
		return GenericJsonArrayParser.create(new FieldJsonParser());
	}
}
