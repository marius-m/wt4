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
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.Votes;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Collection;

public class VotesJsonParser implements JsonObjectParser<Votes> {
	private final BasicVotesJsonParser basicVotesJsonParser = new BasicVotesJsonParser();
	private final BasicUserJsonParser basicUserJsonParser = new BasicUserJsonParser();

	@Override
	public Votes parse(JSONObject json) throws JSONException {
		final BasicVotes basicVotes = basicVotesJsonParser.parse(json);
		final Collection<BasicUser> users = JsonParseUtil.parseJsonArray(json.getJSONArray("voters"), basicUserJsonParser);
		return new Votes(basicVotes.getSelf(), basicVotes.getVotes(), basicVotes.hasVoted(), users);
	}
}
