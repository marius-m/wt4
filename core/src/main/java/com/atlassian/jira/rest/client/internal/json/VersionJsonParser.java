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

import com.atlassian.jira.rest.client.api.domain.Version;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import java.net.URI;

public class VersionJsonParser implements JsonObjectParser<Version> {
	@Override
	public Version parse(JSONObject json) throws JSONException {
		final URI self = JsonParseUtil.getSelfUri(json);
		final Long id = JsonParseUtil.getOptionalLong(json, "id");
		final String name = json.getString("name");
		final String description = JsonParseUtil.getOptionalString(json, "description");
		final boolean isArchived = json.getBoolean("archived");
		final boolean isReleased = json.getBoolean("released");
		final String releaseDateStr = JsonParseUtil.getOptionalString(json, "releaseDate");
		final DateTime releaseDate = parseReleaseDate(releaseDateStr);
		return new Version(self, id, name, description, isArchived, isReleased, releaseDate);
	}

	private DateTime parseReleaseDate(String releaseDateStr) {
		if (releaseDateStr != null) {
			if (releaseDateStr.length() > "YYYY-MM-RR".length()) { // JIRA 4.4 introduces different format - just ISO date
				return JsonParseUtil.parseDateTime(releaseDateStr);
			} else {
				return JsonParseUtil.parseDate(releaseDateStr);
			}
		} else {
			return null;
		}
	}
}
