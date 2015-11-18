/*
 * Copyright (C) 2010-2012 Atlassian
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

package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.domain.util.ErrorCollection;
import com.google.common.base.Objects;

/**
 * Represents error of creating single element during batch operation.
 *
 * @since v1.1
 */

public class BulkOperationErrorResult {
	private final ErrorCollection elementErrors;
	private final Integer failedElementNumber;

	public BulkOperationErrorResult(final ErrorCollection errors, final Integer failedElementNumber) {
		this.elementErrors = errors;
		this.failedElementNumber = failedElementNumber;
	}

	@SuppressWarnings("unused")
	public ErrorCollection getElementErrors() {
		return elementErrors;
	}

	@SuppressWarnings("unused")
	public Integer getFailedElementNumber() {
		return failedElementNumber;
	}


	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("elementErrors", elementErrors)
				.add("failedElementNumber", failedElementNumber)
				.toString();

	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof BulkOperationErrorResult) {
			final BulkOperationErrorResult that = (BulkOperationErrorResult) obj;
			return Objects.equal(this.elementErrors, that.elementErrors)
					&& Objects.equal(this.failedElementNumber, that.failedElementNumber);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(elementErrors, failedElementNumber);
	}
}
