/*
 * Copyright (C) 2011 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.internal.ServerVersionConstants;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class LinkIssuesInputGenerator implements JsonGenerator<LinkIssuesInput> {

	private final ServerInfo serverInfo;

	public LinkIssuesInputGenerator(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	@Override
	public JSONObject generate(LinkIssuesInput linkIssuesInput) throws JSONException {
		JSONObject res = new JSONObject();

		final int buildNumber = serverInfo.getBuildNumber();
		if (buildNumber >= ServerVersionConstants.BN_JIRA_5) {
			res.put("type", new JSONObject().put("name", linkIssuesInput.getLinkType()));
			res.put("inwardIssue", new JSONObject().put("key", linkIssuesInput.getFromIssueKey()));
			res.put("outwardIssue", new JSONObject().put("key", linkIssuesInput.getToIssueKey()));
		} else {
			res.put("linkType", linkIssuesInput.getLinkType());
			res.put("fromIssueKey", linkIssuesInput.getFromIssueKey());
			res.put("toIssueKey", linkIssuesInput.getToIssueKey());
		}
		if (linkIssuesInput.getComment() != null) {
			res.put("comment", new CommentJsonGenerator(serverInfo).generate(linkIssuesInput.getComment()));
		}
		return res;
	}
}
