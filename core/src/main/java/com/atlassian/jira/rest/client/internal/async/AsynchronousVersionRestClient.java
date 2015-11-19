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
package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.jira.rest.client.api.VersionRestClient;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.VersionRelatedIssuesCount;
import com.atlassian.jira.rest.client.api.domain.input.VersionInput;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.VersionJsonParser;
import com.atlassian.jira.rest.client.internal.json.VersionRelatedIssueCountJsonParser;
import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.atlassian.jira.rest.client.internal.json.gen.VersionInputJsonGenerator;
import com.atlassian.jira.rest.client.internal.json.gen.VersionPositionInputGenerator;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.input.VersionPosition;
import com.atlassian.util.concurrent.Promise;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Asynchronous implementation of VersionRestClient.
 *
 * @since v2.0
 */
public class AsynchronousVersionRestClient extends AbstractAsynchronousRestClient implements VersionRestClient {

	private final URI versionRootUri;

	public AsynchronousVersionRestClient(URI baseUri, final HttpClient client) {
		super(client);
		versionRootUri = UriBuilder.fromUri(baseUri).path("version").build();
	}

	@Override
	public Promise<Version> getVersion(final URI versionUri) {
		return getAndParse(versionUri, new VersionJsonParser());
	}

	@Override
	public Promise<Version> createVersion(final VersionInput versionInput) {
		return postAndParse(versionRootUri, versionInput, new VersionInputJsonGenerator(), new VersionJsonParser());
	}

	@Override
	public Promise<Version> updateVersion(final URI versionUri, final VersionInput versionInput) {
		return putAndParse(versionUri, versionInput, new VersionInputJsonGenerator(), new VersionJsonParser());
	}

	@Override
	public Promise<Void> removeVersion(final URI versionUri, final @Nullable URI moveFixIssuesToVersionUri,
			final @Nullable URI moveAffectedIssuesToVersionUri) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(versionUri);
		if (moveFixIssuesToVersionUri != null) {
			uriBuilder.queryParam("moveFixIssuesTo", moveFixIssuesToVersionUri);
		}
		if (moveAffectedIssuesToVersionUri != null) {
			uriBuilder.queryParam("moveAffectedIssuesTo", moveAffectedIssuesToVersionUri);
		}
		return delete(uriBuilder.build());
	}

	@Override
	public Promise<VersionRelatedIssuesCount> getVersionRelatedIssuesCount(final URI versionUri) {
		final URI relatedIssueCountsUri = UriBuilder.fromUri(versionUri).path("relatedIssueCounts").build();
		return getAndParse(relatedIssueCountsUri, new VersionRelatedIssueCountJsonParser());
	}

	@Override
	public Promise<Integer> getNumUnresolvedIssues(final URI versionUri) {
		final URI unresolvedIssueCountUri = UriBuilder.fromUri(versionUri).path("unresolvedIssueCount").build();
		return getAndParse(unresolvedIssueCountUri, new JsonObjectParser<Integer>() {
			@Override
			public Integer parse(JSONObject json) throws JSONException {
				return json.getInt("issuesUnresolvedCount");
			}
		});
	}

	@Override
	public Promise<Version> moveVersionAfter(final URI versionUri, final URI afterVersionUri) {
		final URI moveUri = getMoveVersionUri(versionUri);

		return postAndParse(moveUri, afterVersionUri, new JsonGenerator<URI>() {
			@Override
			public JSONObject generate(final URI uri) throws JSONException {
				final JSONObject res = new JSONObject();
				res.put("after", uri);
				return res;
			}
		}, new VersionJsonParser());
	}

	@Override
	public Promise<Version> moveVersion(final URI versionUri, final VersionPosition versionPosition) {
		final URI moveUri = getMoveVersionUri(versionUri);
		return postAndParse(moveUri, versionPosition, new VersionPositionInputGenerator(), new VersionJsonParser());
	}

	private URI getMoveVersionUri(URI versionUri) {
		return UriBuilder.fromUri(versionUri).path("move").build();
	}

}
