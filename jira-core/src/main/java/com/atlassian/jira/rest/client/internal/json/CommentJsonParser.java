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
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

public class CommentJsonParser implements JsonObjectParser<Comment> {

	public static final String VISIBILITY_KEY = "visibility";
	private final VisibilityJsonParser visibilityJsonParser = new VisibilityJsonParser();

	@Override
	public Comment parse(JSONObject json) throws JSONException {
		final URI selfUri = JsonParseUtil.getSelfUri(json);
		final Long id = JsonParseUtil.getOptionalLong(json, "id");
		final String body = json.getString("body");
		final BasicUser author = JsonParseUtil.parseBasicUser(json.optJSONObject("author"));
		final BasicUser updateAuthor = JsonParseUtil.parseBasicUser(json.optJSONObject("updateAuthor"));

		final Visibility visibility = visibilityJsonParser.parseVisibility(json);
		return new Comment(selfUri, body, author, updateAuthor, JsonParseUtil.parseDateTime(json.getString("created")),
				JsonParseUtil.parseDateTime(json.getString("updated")), visibility, id);
	}
}
