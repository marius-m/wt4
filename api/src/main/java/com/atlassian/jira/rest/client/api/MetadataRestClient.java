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

package com.atlassian.jira.rest.client.api;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.IssuelinksType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;

/**
 * Serves information about JIRA metadata like server information, issue types defined, stati, priorities and resolutions.
 * This data constitutes a data dictionary which then JIRA issues base on.
 *
 * @since v2.0
 */
public interface MetadataRestClient {

	/**
	 * Retrieves from the server complete information about selected issue type
	 *
	 * @param uri URI to issue type resource (one can get it e.g. from <code>self</code> attribute
	 *            of issueType field of an issue).
	 * @return complete information about issue type resource
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<IssueType> getIssueType(URI uri);

	/**
	 * Retrieves from the server complete list of available issue type
	 *
	 * @return complete information about issue type resource
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 * @since com.atlassian.jira.rest.client.api 1.0, server 5.0
	 */
	Promise<Iterable<IssueType>> getIssueTypes();

	/**
	 * Retrieves from the server complete list of available issue types
	 *
	 * @return list of available issue types for this JIRA instance
	 * @throws RestClientException in case of problems (if linking is disabled on the server, connectivity, malformed messages, etc.)
	 * @since server 4.3, com.atlassian.jira.rest.client.api 0.5
	 */
	Promise<Iterable<IssuelinksType>> getIssueLinkTypes();

	/**
	 * Retrieves complete information about selected status
	 *
	 * @param uri URI to this status resource (one can get it e.g. from <code>self</code> attribute
	 *            of <code>status</code> field of an issue)
	 * @return complete information about the selected status
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<Status> getStatus(URI uri);

	/**
	 * Retrieves lists of available statuses with complete information about them
	 *
	 * @return Lists of complete information about available statuses
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<Iterable<Status>> getStatuses();

	/**
	 * Retrieves from the server complete information about selected priority
	 *
	 * @param uri URI for the priority resource
	 * @return complete information about the selected priority
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<Priority> getPriority(URI uri);

	/**
	 * Retrieves from the server complete list of available priorities
	 *
	 * @return complete information about the selected priority
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 * @since com.atlassian.jira.rest.client.api 1.0, server 5.0
	 */
	Promise<Iterable<Priority>> getPriorities();

	/**
	 * Retrieves from the server complete information about selected resolution
	 *
	 * @param uri URI for the resolution resource
	 * @return complete information about the selected resolution
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<Resolution> getResolution(URI uri);

	/**
	 * Retrieves from the server complete information about selected resolution
	 *
	 * @return complete information about the selected resolution
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 * @since com.atlassian.jira.rest.client.api 1.0, server 5.0
	 */
	Promise<Iterable<Resolution>> getResolutions();

	/**
	 * Retrieves information about this JIRA instance
	 *
	 * @return information about this JIRA instance
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<ServerInfo> getServerInfo();

	/**
	 * Retrieves information about JIRA custom and system fields.
	 *
	 * @return information about JIRA custom and system fields.
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 */
	Promise<Iterable<Field>> getFields();
}
