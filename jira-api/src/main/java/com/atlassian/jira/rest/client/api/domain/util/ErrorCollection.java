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

package com.atlassian.jira.rest.client.api.domain.util;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Error container returned in bulk operations
 *
 * @since v2.0
 */
public class ErrorCollection {

	private final Integer status;
	private final Collection<String> errorMessages;
	private final Map<String, String> errors;

	public ErrorCollection(@Nullable final Integer status, final Collection<String> errorMessages, final Map<String, String> errors) {
		this.status = status;
		this.errors = ImmutableMap.copyOf(errors);
		this.errorMessages = ImmutableList.copyOf(errorMessages);
	}

	public ErrorCollection(final String errorMessage) {
		this(null, ImmutableList.of(errorMessage), Collections.<String, String>emptyMap());
	}

	@SuppressWarnings("unused")
	@Nullable
	public Integer getStatus() {
		return status;
	}

	public Collection<String> getErrorMessages() {
		return errorMessages;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("status", status)
				.add("errors", errors)
				.add("errorMessages", errorMessages)
				.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ErrorCollection) {
			final ErrorCollection that = (ErrorCollection) obj;
			return Objects.equal(this.status, that.status)
					&& Objects.equal(this.errors, that.errors)
					&& Objects.equal(this.errorMessages, that.errorMessages);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(status, errors, errorMessages);
	}

	public static class Builder {

		private int status;
		private final ImmutableMap.Builder<String, String> errors;
		private final ImmutableList.Builder<String> errorMessages;

		public Builder() {
			errors = ImmutableMap.builder();
			errorMessages = ImmutableList.builder();
		}

		public Builder status(final int status) {
			this.status = status;
			return this;
		}

		public Builder error(final String key, final String message) {
			errors.put(key, message);
			return this;

		}

		public Builder errorMessage(final String message) {
			errorMessages.add(message);
			return this;
		}

		public ErrorCollection build() {
			return new ErrorCollection(status, errorMessages.build(), errors.build());
		}
	}
}
