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

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.internal.json.*;
import com.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Asynchronous implementation of MetadataRestClient.
 *
 * @since v2.0
 */
public class AsynchronousMetadataRestClient extends AbstractAsynchronousRestClient implements MetadataRestClient {

	private static final String SERVER_INFO_RESOURCE = "/serverInfo";
	private final ServerInfoJsonParser serverInfoJsonParser = new ServerInfoJsonParser();
	private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
	private final GenericJsonArrayParser<IssueType> issueTypesJsonParser = GenericJsonArrayParser.create(issueTypeJsonParser);
	private final StatusJsonParser statusJsonParser = new StatusJsonParser();
	private final GenericJsonArrayParser<Status> statusesJsonParser = GenericJsonArrayParser.create(statusJsonParser);
	private final PriorityJsonParser priorityJsonParser = new PriorityJsonParser();
	private final GenericJsonArrayParser<Priority> prioritiesJsonParser = GenericJsonArrayParser.create(priorityJsonParser);
	private final ResolutionJsonParser resolutionJsonParser = new ResolutionJsonParser();
	private final GenericJsonArrayParser<Resolution> resolutionsJsonParser = GenericJsonArrayParser.create(resolutionJsonParser);
	private final IssueLinkTypesJsonParser issueLinkTypesJsonParser = new IssueLinkTypesJsonParser();
	private final JsonArrayParser<Iterable<Field>> fieldsJsonParser = FieldJsonParser.createFieldsArrayParser();
	private final URI baseUri;

	public AsynchronousMetadataRestClient(final URI baseUri, HttpClient httpClient) {
		super(httpClient);
		this.baseUri = baseUri;

	}

	@Override
	public Promise<IssueType> getIssueType(final URI uri) {
		return getAndParse(uri, issueTypeJsonParser);
	}

	@Override
	public Promise<Iterable<IssueType>> getIssueTypes() {
		final URI uri = UriBuilder.fromUri(baseUri).path("issuetype").build();
		return getAndParse(uri, issueTypesJsonParser);
	}

	@Override
	public Promise<Iterable<IssuelinksType>> getIssueLinkTypes() {
		final URI uri = UriBuilder.fromUri(baseUri).path("issueLinkType").build();
		return getAndParse(uri, issueLinkTypesJsonParser);
	}

	@Override
	public Promise<Status> getStatus(URI uri) {
		return getAndParse(uri, statusJsonParser);
	}

	@Override
	public Promise<Iterable<Status>> getStatuses() {
		final URI uri = UriBuilder.fromUri(baseUri).path("status").build();
		return getAndParse(uri, statusesJsonParser);
	}

	@Override
	public Promise<Priority> getPriority(URI uri) {
		return getAndParse(uri, priorityJsonParser);
	}

	@Override
	public Promise<Iterable<Priority>> getPriorities() {
		final URI uri = UriBuilder.fromUri(baseUri).path("priority").build();
		return getAndParse(uri, prioritiesJsonParser);
	}

	@Override
	public Promise<Resolution> getResolution(URI uri) {
		return getAndParse(uri, resolutionJsonParser);
	}

	@Override
	public Promise<Iterable<Resolution>> getResolutions() {
		final URI uri = UriBuilder.fromUri(baseUri).path("resolution").build();
		return getAndParse(uri, resolutionsJsonParser);
	}

	@Override
	public Promise<ServerInfo> getServerInfo() {
		final URI serverInfoUri = UriBuilder.fromUri(baseUri).path(SERVER_INFO_RESOURCE).build();
		return getAndParse(serverInfoUri, serverInfoJsonParser);
	}

	@Override
	public Promise<Iterable<Field>> getFields() {
		final URI uri = UriBuilder.fromUri(baseUri).path("field").build();
		return getAndParse(uri, fieldsJsonParser);
	}
}
