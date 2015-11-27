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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.format.DateTimeFormatter;

public class WorklogInputJsonGenerator implements JsonGenerator<WorklogInput> {

	private final JsonGenerator<Visibility> visibilityGenerator = new VisibilityJsonGenerator();
	private final JsonGenerator<BasicUser> basicUserJsonGenerator = new BasicUserJsonGenerator();
	private final DateTimeFormatter dateTimeFormatter;

	public WorklogInputJsonGenerator() {
		this(JsonParseUtil.JIRA_DATE_TIME_FORMATTER);
	}

	public WorklogInputJsonGenerator(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

	@Override
	public JSONObject generate(final WorklogInput worklogInput) throws JSONException {
		final JSONObject res = new JSONObject()
				.put("self", worklogInput.getSelf())
				.put("comment", worklogInput.getComment())
				.put("started", dateTimeFormatter.print(worklogInput.getStartDate()))
				.put("timeSpent", worklogInput.getMinutesSpent() + "m");

		putGeneratedIfNotNull("visibility", worklogInput.getVisibility(), res, visibilityGenerator);
		putGeneratedIfNotNull("author", worklogInput.getAuthor(), res, basicUserJsonGenerator);
		putGeneratedIfNotNull("updateAuthor", worklogInput.getUpdateAuthor(), res, basicUserJsonGenerator);
		return res;
	}

	private <K> JSONObject putGeneratedIfNotNull(final String key, final K value, final JSONObject dest, final JsonGenerator<K> generator)
			throws JSONException {
		if (value != null) {
			dest.put(key, generator.generate(value));
		}
		return dest;
	}
}
