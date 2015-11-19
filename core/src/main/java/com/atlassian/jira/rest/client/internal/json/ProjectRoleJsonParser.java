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

import com.atlassian.jira.rest.client.api.domain.ProjectRole;
import com.atlassian.jira.rest.client.api.domain.RoleActor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.Collection;

public class ProjectRoleJsonParser implements JsonObjectParser<ProjectRole> {

	private final RoleActorJsonParser roleActorJsonParser;

	public ProjectRoleJsonParser(URI baseJiraUri) {
		this.roleActorJsonParser = new RoleActorJsonParser(baseJiraUri);
	}

	@Override
	public ProjectRole parse(final JSONObject json) throws JSONException {
		final URI self = JsonParseUtil.getSelfUri(json);
		final long id = json.getLong("id");
		final String name = json.getString("name");
		final String description = json.getString("description");
		final Optional<JSONArray> roleActorsOpt = JsonParseUtil.getOptionalArray(json, "actors");
		final Collection<RoleActor> roleActors = roleActorsOpt.isPresent() ?
				JsonParseUtil.parseJsonArray(roleActorsOpt.get(), roleActorJsonParser) : ImmutableSet.<RoleActor>of();
		return new ProjectRole(id, self, name, description, roleActors);
	}

}
