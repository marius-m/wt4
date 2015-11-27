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

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Map;

/**
 * Json Generator for ComplexIssueInputFieldValue
 *
 * @since v1.0
 */
public class ComplexIssueInputFieldValueJsonGenerator implements JsonGenerator<ComplexIssueInputFieldValue> {
	@Override
	public JSONObject generate(ComplexIssueInputFieldValue bean) throws JSONException {
		final JSONObject json = new JSONObject();
		for (Map.Entry<String, Object> entry : bean.getValuesMap().entrySet()) {
			json.put(entry.getKey(), generateFieldValueForJson(entry.getValue()));
		}
		return json;
	}

	public Object generateFieldValueForJson(Object rawValue) throws JSONException {
		if (rawValue == null) {
			return JSONObject.NULL;
		} else if (rawValue instanceof ComplexIssueInputFieldValue) {
			return generate((ComplexIssueInputFieldValue) rawValue);
		} else if (rawValue instanceof Iterable) {
			// array with values
			final JSONArray array = new JSONArray();
			for (Object value : (Iterable) rawValue) {
				array.put(generateFieldValueForJson(value));
			}
			return array;
		} else if (rawValue instanceof CharSequence) {
			return rawValue.toString();
		} else if (rawValue instanceof Number) {
			return rawValue;
		} else {
			throw new JSONException("Cannot generate value - unknown type for me: " + rawValue.getClass());
		}
	}
}
