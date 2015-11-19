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

import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

/**
 * @since v0.1
 */
public class BasicVotesJsonParser implements JsonObjectParser<BasicVotes> {
	@Override
	public BasicVotes parse(JSONObject json) throws JSONException {
		final URI self = JsonParseUtil.getSelfUri(json);
		final int voteCount = json.getInt("votes");
		final boolean hasVoted = json.getBoolean("hasVoted");
		return new BasicVotes(self, voteCount, hasVoted);
	}
}
