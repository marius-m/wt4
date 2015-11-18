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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IssueFieldJsonParser {
	private static final String VALUE_ATTRIBUTE = "value";

	private Map<String, JsonObjectParser> registeredValueParsers = new HashMap<String, JsonObjectParser>() {{
		put("com.atlassian.jira.plugin.system.customfieldtypes:float", new FloatingPointFieldValueParser());
		put("com.atlassian.jira.plugin.system.customfieldtypes:userpicker", new FieldValueJsonParser<BasicUser>(new BasicUserJsonParser()));
		put("java.lang.String", new StringFieldValueParser());
	}};

	@SuppressWarnings("unchecked")
	public IssueField parse(JSONObject jsonObject, String id) throws JSONException {
		String type = jsonObject.getString("type");
		final String name = jsonObject.getString("name");
		final Object valueObject = jsonObject.opt(VALUE_ATTRIBUTE);
		final Object value;
		// @todo ugly hack until https://jdog.atlassian.com/browse/JRADEV-3220 is fixed
		if ("comment".equals(name)) {
			type = "com.atlassian.jira.Comment";
		}

		if (valueObject == null) {
			value = null;
		} else {
			final JsonObjectParser valueParser = registeredValueParsers.get(type);
			if (valueParser != null) {
				value = valueParser.parse(jsonObject);
			} else {
				value = valueObject.toString();
			}
		}
		return new IssueField(id, name, type, value);
	}

	static class FieldValueJsonParser<T> implements JsonObjectParser<T> {
		private final JsonObjectParser<T> jsonParser;

		public FieldValueJsonParser(JsonObjectParser<T> jsonParser) {
			this.jsonParser = jsonParser;
		}

		@Override
		public T parse(JSONObject json) throws JSONException {
			final JSONObject valueObject = json.optJSONObject(VALUE_ATTRIBUTE);
			if (valueObject == null) {
				throw new JSONException("Expected JSONObject with [" + VALUE_ATTRIBUTE + "] attribute present.");
			}
			return jsonParser.parse(valueObject);
		}
	}


	static class FloatingPointFieldValueParser implements JsonObjectParser<Double> {

		@Override
		public Double parse(JSONObject jsonObject) throws JSONException {
			final String s = JsonParseUtil.getNullableString(jsonObject, VALUE_ATTRIBUTE);
			if (s == null) {
				return null;
			}
			try {
				return Double.parseDouble(s);
			} catch (NumberFormatException e) {
				throw new JSONException("[" + s + "] is not a valid floating point number");
			}
		}
	}

	static class StringFieldValueParser implements JsonObjectParser<String> {

		@Override
		public String parse(JSONObject jsonObject) throws JSONException {
			return JsonParseUtil.getNullableString(jsonObject, VALUE_ATTRIBUTE);
		}
	}


}
