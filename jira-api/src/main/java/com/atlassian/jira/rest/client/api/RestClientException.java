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

import com.atlassian.jira.rest.client.api.domain.util.ErrorCollection;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;

/**
 * Basic exception which may be thrown by any remote operation encapsulated by the REST com.atlassian.jira.rest.client.api.
 * Usually some more specific exception will be chained here and available via {@link #getCause()}
 *
 * @since v0.1
 */
public class RestClientException extends RuntimeException {

	private final Optional<Integer> statusCode;
	private final Collection<ErrorCollection> errorCollections;

	public RestClientException(final RestClientException exception) {
		super(exception.getMessage(), exception);
		this.statusCode = exception.getStatusCode();
		this.errorCollections = exception.errorCollections;
	}

	public RestClientException(final Throwable cause) {
		super(cause);
		this.errorCollections = Collections.emptyList();
		this.statusCode = Optional.absent();
	}

	public RestClientException(final Throwable cause, final int statusCode) {
		super(cause);
		this.errorCollections = Collections.emptyList();
		this.statusCode = Optional.of(statusCode);
	}

	public RestClientException(final String errorMessage, final Throwable cause) {
		super(errorMessage, cause);
		this.errorCollections = ImmutableList.of(new ErrorCollection(errorMessage));
		statusCode = Optional.absent();
	}

	public RestClientException(final Collection<ErrorCollection> errorCollections, final int statusCode) {
		super(errorCollections.toString());
		this.errorCollections = ImmutableList.copyOf(errorCollections);
		this.statusCode = Optional.of(statusCode);
	}

	public RestClientException(final Collection<ErrorCollection> errorCollections, final Throwable cause, final int statusCode) {
		super(errorCollections.toString(), cause);
		this.errorCollections = ImmutableList.copyOf(errorCollections);
		this.statusCode = Optional.of(statusCode);
	}

	/**
	 * @return error messages used while building this exception object
	 */
	public Collection<ErrorCollection> getErrorCollections() {
		return errorCollections;
	}

	/**
	 * @return optional error code of failed http request.
	 */
	public Optional<Integer> getStatusCode() {
		return statusCode;
	}

	@Override
	public String toString() {
		return "RestClientException{" +
				"statusCode=" + statusCode +
				", errorCollections=" + errorCollections +
				'}';
	}
}
