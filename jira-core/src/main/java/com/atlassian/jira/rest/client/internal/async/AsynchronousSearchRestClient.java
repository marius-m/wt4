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

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.internal.json.FilterJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.SearchResultJsonParser;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Set;

import static com.atlassian.jira.rest.client.api.IssueRestClient.Expandos.NAMES;
import static com.atlassian.jira.rest.client.api.IssueRestClient.Expandos.SCHEMA;

/**
 * Asynchronous implementation of SearchRestClient.
 *
 * @since v2.0
 */
public class AsynchronousSearchRestClient extends AbstractAsynchronousRestClient implements SearchRestClient {

	private static final Function<IssueRestClient.Expandos, String> EXPANDO_TO_PARAM = new Function<IssueRestClient.Expandos, String>() {
		@Override
		public String apply(IssueRestClient.Expandos from) {
			return from.name().toLowerCase();
		}
	};

	private static final String START_AT_ATTRIBUTE = "startAt";
	private static final String MAX_RESULTS_ATTRIBUTE = "maxResults";
	private static final int MAX_JQL_LENGTH_FOR_HTTP_GET = 500;
	private static final String JQL_ATTRIBUTE = "jql";
	private static final String FILTER_FAVOURITE_PATH = "filter/favourite";
	private static final String FILTER_PATH_FORMAT = "filter/%s";
	private static final String SEARCH_URI_PREFIX = "search";
	private static final String EXPAND_ATTRIBUTE = "expand";
	private static final String FIELDS_ATTRIBUTE = "fields";

	private final SearchResultJsonParser searchResultJsonParser = new SearchResultJsonParser();
	private final FilterJsonParser filterJsonParser = new FilterJsonParser();
	private final GenericJsonArrayParser<Filter> filtersParser = GenericJsonArrayParser.create(new FilterJsonParser());

	private final URI searchUri;
	private final URI favouriteUri;
	private final URI baseUri;

	public AsynchronousSearchRestClient(final URI baseUri, final HttpClient asyncHttpClient) {
		super(asyncHttpClient);
		this.baseUri = baseUri;
		this.searchUri = UriBuilder.fromUri(baseUri).path(SEARCH_URI_PREFIX).build();
		this.favouriteUri = UriBuilder.fromUri(baseUri).path(FILTER_FAVOURITE_PATH).build();
	}

	@Override
	public Promise<SearchResult> searchJql(@Nullable String jql) {
		return searchJql(jql, null, null, null);
	}

	public Promise<SearchResult> searchJql(@Nullable String jql, @Nullable Integer maxResults, @Nullable Integer startAt, @Nullable Set<String> fields) {
		final Iterable<String> expandosValues = Iterables.transform(ImmutableList.of(SCHEMA, NAMES), EXPANDO_TO_PARAM);
		final String notNullJql = StringUtils.defaultString(jql);
		if (notNullJql.length() > MAX_JQL_LENGTH_FOR_HTTP_GET) {
			return searchJqlImplPost(maxResults, startAt, expandosValues, notNullJql, fields);
		} else {
			return searchJqlImplGet(maxResults, startAt, expandosValues, notNullJql, fields);
		}
	}

	private Promise<SearchResult> searchJqlImplGet(@Nullable Integer maxResults, @Nullable Integer startAt, Iterable<String> expandosValues, String jql, @Nullable Set<String> fields) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(searchUri)
				.queryParam(JQL_ATTRIBUTE, jql)
				.queryParam(EXPAND_ATTRIBUTE, Joiner.on(",").join(expandosValues));

		if (fields != null) {
			uriBuilder.queryParam(FIELDS_ATTRIBUTE, Joiner.on(",").join(fields));
		}
		addOptionalQueryParam(uriBuilder, MAX_RESULTS_ATTRIBUTE, maxResults);
		addOptionalQueryParam(uriBuilder, START_AT_ATTRIBUTE, startAt);

		return getAndParse(uriBuilder.build(), searchResultJsonParser);
	}

	private void addOptionalQueryParam(final UriBuilder uriBuilder, final String key, final Object... values) {
		if (values != null && values.length > 0 && values[0] != null) {
			uriBuilder.queryParam(key, values);
		}
	}

	private Promise<SearchResult> searchJqlImplPost(@Nullable Integer maxResults, @Nullable Integer startAt, Iterable<String> expandosValues, String jql, @Nullable Set<String> fields) {
		final JSONObject postEntity = new JSONObject();

		try {
			postEntity.put(JQL_ATTRIBUTE, jql)
					.put(EXPAND_ATTRIBUTE, ImmutableList.copyOf(expandosValues))
					.putOpt(START_AT_ATTRIBUTE, startAt)
					.putOpt(MAX_RESULTS_ATTRIBUTE, maxResults);

			if (fields != null) {
				postEntity.put(FIELDS_ATTRIBUTE, fields); // putOpt doesn't work with collections
			}
		} catch (JSONException e) {
			throw new RestClientException(e);
		}
		return postAndParse(searchUri, postEntity, searchResultJsonParser);
	}

	@Override
	public Promise<Iterable<Filter>> getFavouriteFilters() {
		return getAndParse(favouriteUri, filtersParser);
	}

	@Override
	public Promise<Filter> getFilter(URI filterUri) {
		return getAndParse(filterUri, filterJsonParser);
	}

	@Override
	public Promise<Filter> getFilter(long id) {
		return getFilter(UriBuilder.fromUri(baseUri).path(String.format(FILTER_PATH_FORMAT, id)).build());
	}
}
