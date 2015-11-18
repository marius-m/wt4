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

package com.atlassian.jira.rest.client.api.domain;

import com.google.common.base.Objects;

/**
 * Represents search results - links to issues matching given filter (JQL query) with basic
 * information supporting the paging through the results.
 *
 * @since v0.2
 */
public class SearchResult {
	private final int startIndex;
	private final int maxResults;
	private final int total;
	private final Iterable<Issue> issues;

	public SearchResult(int startIndex, int maxResults, int total, Iterable<Issue> issues) {
		this.startIndex = startIndex;
		this.maxResults = maxResults;
		this.total = total;
		this.issues = issues;
	}

	/**
	 * @return 0-based start index of the returned issues (e.g. "3" means that 4th, 5th...maxResults issues matching given query
	 *         have been returned.
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * @return maximum page size (the window to results).
	 */
	public int getMaxResults() {
		return maxResults;
	}

	/**
	 * @return total number of issues (regardless of current maxResults and startIndex) matching given criteria.
	 *         Query JIRA another time with different startIndex to get subsequent issues
	 */
	public int getTotal() {
		return total;
	}

	public Iterable<Issue> getIssues() {
		return issues;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("startIndex", startIndex).
				add("maxResults", maxResults).
				add("total", total).
				add("issues", issues).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SearchResult) {
			SearchResult that = (SearchResult) obj;
			return Objects.equal(this.startIndex, that.startIndex)
					&& Objects.equal(this.maxResults, that.maxResults)
					&& Objects.equal(this.total, that.total)
					&& Objects.equal(this.issues, that.issues);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(startIndex, maxResults, total, issues);
	}

}
