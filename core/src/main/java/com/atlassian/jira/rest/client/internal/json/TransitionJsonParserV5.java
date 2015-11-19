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

import com.atlassian.jira.rest.client.api.domain.Transition;
import com.google.common.collect.Lists;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Collection;
import java.util.Iterator;

public class TransitionJsonParserV5 implements JsonObjectParser<Transition> {
	private final TransitionFieldJsonParser transitionFieldJsonParser = new TransitionFieldJsonParser();

	public Transition parse(JSONObject json) throws JSONException {
		final int id = json.getInt("id");
		final String name = json.getString("name");
		final JSONObject fieldsObj = json.getJSONObject("fields");
		final Iterator keys = fieldsObj.keys();
		final Collection<Transition.Field> fields = Lists.newArrayList();
		while (keys.hasNext()) {
			final String fieldId = keys.next().toString();
			fields.add(transitionFieldJsonParser.parse(fieldsObj.getJSONObject(fieldId), fieldId));
		}
		return new Transition(name, id, fields);
	}

	public static class TransitionFieldJsonParser {
		public Transition.Field parse(JSONObject json, final String id) throws JSONException {
			final boolean isRequired = json.getBoolean("required");
			final String type = json.getJSONObject("schema").getString("type");
			return new Transition.Field(id, isRequired, type);
		}
	}
}
