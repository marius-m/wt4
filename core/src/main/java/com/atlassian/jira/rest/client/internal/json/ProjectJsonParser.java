/*
 * Copyright (C) 2010-2012 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.BasicProjectRole;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.google.common.base.Splitter;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

public class ProjectJsonParser implements JsonObjectParser<Project> {

	private final VersionJsonParser versionJsonParser = new VersionJsonParser();
	private final BasicComponentJsonParser componentJsonParser = new BasicComponentJsonParser();
	private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
	private final BasicProjectRoleJsonParser basicProjectRoleJsonParser = new BasicProjectRoleJsonParser();

    static Iterable<String> parseExpandos(final JSONObject json) throws JSONException
    {
        if (json.has("expand")) {
            final String expando = json.getString("expand");
            return Splitter.on(',').split(expando);
        } else {
            return Collections.emptyList();
        }
    }

	@Override
	public Project parse(JSONObject json) throws JSONException {
        URI self = JsonParseUtil.getSelfUri(json);
        final Iterable<String> expandos = parseExpandos(json);
        final BasicUser lead = JsonParseUtil.parseBasicUser(json.getJSONObject("lead"));
		final String key = json.getString("key");
		final Long id = JsonParseUtil.getOptionalLong(json, "id");
		final String name = JsonParseUtil.getOptionalString(json, "name");
		final String urlStr = JsonParseUtil.getOptionalString(json, "url");
		URI uri;
		try {
			uri = urlStr == null || "".equals(urlStr) ? null : new URI(urlStr);
		} catch (URISyntaxException e) {
			uri = null;
		}
		String description = JsonParseUtil.getOptionalString(json, "description");
		if ("".equals(description)) {
			description = null;
		}
		final Collection<Version> versions = JsonParseUtil.parseJsonArray(json.getJSONArray("versions"), versionJsonParser);
		final Collection<BasicComponent> components = JsonParseUtil.parseJsonArray(json
				.getJSONArray("components"), componentJsonParser);
		final JSONArray issueTypesArray = json.optJSONArray("issueTypes");
		final OptionalIterable<IssueType> issueTypes = JsonParseUtil.parseOptionalJsonArray(issueTypesArray, issueTypeJsonParser);
		final Collection<BasicProjectRole> projectRoles = basicProjectRoleJsonParser.parse(JsonParseUtil
				.getOptionalJsonObject(json, "roles"));
        return new Project(expandos, self, key, id, name, description, lead, uri, versions, components, issueTypes, projectRoles);
	}


}
