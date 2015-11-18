/*
 * Copyright (C) 2012 Atlassian
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
 * Basic representation of a JIRA issues and errors created using batch operation.
 *
 * @since v2.0
 */
public class BulkOperationResult<T> {

	private final Iterable<T> issues;
	private final Iterable<BulkOperationErrorResult> errors;

	public BulkOperationResult(final Iterable<T> issues, final Iterable<BulkOperationErrorResult> errors) {
		this.issues = issues;
		this.errors = errors;
	}

	public Iterable<T> getIssues() {
		return issues;
	}

	public Iterable<BulkOperationErrorResult> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("issues", issues)
				.add("errors", errors)
				.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof BulkOperationResult) {
			final BulkOperationResult that = (BulkOperationResult) obj;
			return Objects.equal(this.issues, that.issues)
					&& Objects.equal(this.errors, that.errors);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(issues, errors);
	}
}
