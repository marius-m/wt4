/*
 * Copyright (C) 2014 Atlassian
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

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Represents operations returned for expand {@link IssueRestClient.Expandos#OPERATIONS}
 *
 * @since 2.0
 */
public class Operations {
	private final Iterable<OperationGroup> linkGroups;

	public Operations(final Iterable<OperationGroup> linkGroups) {
		this.linkGroups = linkGroups;
	}

	public Iterable<OperationGroup> getLinkGroups() {
		return linkGroups;
	}

	public <T> Optional<T> accept(OperationVisitor<T> visitor) {
		return OperationGroup.accept(getLinkGroups(), visitor);
	}

	public Operation getOperationById(final String operationId) {
		return accept(new OperationVisitor<Operation>() {
			@Override
			public Optional<Operation> visit(Operation operation) {
				return operationId.equals(operation.getId()) ? Optional.of(operation) : Optional.<Operation>absent();
			}
		}).orNull();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(linkGroups);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final Operations other = (Operations) obj;
		return Iterables.elementsEqual(this.linkGroups, other.linkGroups);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("linkGroups", linkGroups)
				.toString();
	}
}
