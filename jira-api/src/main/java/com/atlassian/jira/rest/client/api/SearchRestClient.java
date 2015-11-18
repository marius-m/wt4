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

package com.atlassian.jira.rest.client.api;

import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Set;

/**
 * The client handling search REST resource
 *
 * @since 2.0 client, 4.3 server
 */
public interface SearchRestClient {
	/**
	 * Performs a JQL search and returns issues matching the query
	 *
	 * @param jql a valid JQL query (will be properly encoded by JIRA client). Restricted JQL characters (like '/') must be properly escaped.
	 * @return issues matching given JQL query
	 * @throws RestClientException in case of problems (connectivity, malformed messages, invalid JQL query, etc.)
	 */
	Promise<SearchResult> searchJql(@Nullable String jql);

	/**
	 * Performs a JQL search and returns issues matching the query. The first startAt issues will be skipped and SearchResult will
	 * contain at most maxResults issues. List of issue fields which should be included in the result may be specified.
	 *
	 * @param jql        a valid JQL query (will be properly encoded by JIRA client). Restricted JQL characters (like '/') must
	 *                   be properly escaped. All issues matches to the null or empty JQL.
	 * @param maxResults maximum results for this search. When null is given, the default maxResults configured in JIRA is
	 *                   used (usually 50).
	 * @param startAt    starting index (0-based) defining how many issues should be skipped in the results. For example for
	 *                   startAt=5 and maxResults=3 the results will include matching issues with index 5, 6 and 7.
	 *                   For startAt = 0 and maxResults=3 the issues returned are from position 0, 1 and 2.
	 *                   When null is given, the default startAt is used (0).
	 * @param fields     set of fields which should be retrieved. You can specify *all for all fields
	 *                   or *navigable (which is the default value, used when null is given) which will cause to include only
	 *                   navigable fields in the result. To ignore the specific field you can use "-" before the field's name.
	 *                   Note that the following fields: summary, issuetype, created, updated, project and status are
	 *                   required. These fields are included in *all and *navigable.
	 * @return issues matching given JQL query
	 * @throws RestClientException in case of problems (connectivity, malformed messages, invalid JQL query, etc.)
	 */
	Promise<SearchResult> searchJql(@Nullable String jql, @Nullable Integer maxResults, @Nullable Integer startAt, @Nullable Set<String> fields);

	/**
	 * Retrieves list of your favourite filters.
	 *
	 * @return list of your favourite filters
	 * @since 2.0 client, 5.0 server
	 */
	Promise<Iterable<Filter>> getFavouriteFilters();

	/**
	 * Retrieves filter for given URI.
	 *
	 * @param filterUri URI to filter resource (usually get from <code>self</code> attribute describing component elsewhere)
	 * @return filter
	 * @since 2.0 client, 5.0 server
	 */
	Promise<Filter> getFilter(URI filterUri);

	/**
	 * Retrieves filter for given id.
	 *
	 * @param id ID of the filter
	 * @return filter
	 * @since 2.0 client, 5.0 server
	 */
	Promise<Filter> getFilter(long id);
}
