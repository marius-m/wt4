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

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.internal.ServerVersionConstants;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.internal.json.CommentJsonParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class CommentJsonGenerator implements JsonGenerator<Comment> {

	private final ServerInfo serverInfo;

	public CommentJsonGenerator(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	@Override
	public JSONObject generate(Comment comment) throws JSONException {
		JSONObject res = new JSONObject();
		if (comment.getBody() != null) {
			res.put("body", comment.getBody());
		}

		final Visibility commentVisibility = comment.getVisibility();
		if (commentVisibility != null) {

			final int buildNumber = serverInfo.getBuildNumber();
			if (buildNumber >= ServerVersionConstants.BN_JIRA_4_3) {
				JSONObject visibilityJson = new JSONObject();
				final String commentVisibilityType;
				if (buildNumber >= ServerVersionConstants.BN_JIRA_5) {
					commentVisibilityType = commentVisibility.getType() == Visibility.Type.GROUP ? "group" : "role";
				} else {
					commentVisibilityType = commentVisibility.getType() == Visibility.Type.GROUP ? "GROUP" : "ROLE";
				}
				visibilityJson.put("type", commentVisibilityType);
				visibilityJson.put("value", commentVisibility.getValue());
				res.put(CommentJsonParser.VISIBILITY_KEY, visibilityJson);
			} else {
				if (commentVisibility.getType() == Visibility.Type.ROLE) {
					res.put("role", commentVisibility.getValue());
				} else {
					res.put("group", commentVisibility.getValue());
				}
			}
		}

		return res;
	}
}
