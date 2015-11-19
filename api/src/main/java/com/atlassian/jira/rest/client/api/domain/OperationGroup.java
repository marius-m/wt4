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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * Represents operations group
 *
 * @since 2.0
 */
public class OperationGroup implements Operation {
	@Nullable private final String id;
	@Nullable private final OperationHeader header;
	private final Iterable<OperationLink> links;
	private final Iterable<OperationGroup> groups;
	@Nullable private final Integer weight;

	public OperationGroup(@Nullable final String id, final Iterable<OperationLink> links,
			final Iterable<OperationGroup> groups, @Nullable final OperationHeader header,
			@Nullable final Integer weight) {
		this.id = id;
		this.header = header;
		this.links = links;
		this.groups = groups;
		this.weight = weight;
	}

	@Nullable
	public String getId() {
		return id;
	}

	@Override
	public <T> Optional<T> accept(final OperationVisitor<T> visitor) {
		final Optional<T> result = visitor.visit(this);
		if (result.isPresent()) {
			return result;
		} else {
			final Iterable<Operation> operations = Iterables.concat(
					header != null ? Collections.singleton(header) : Collections.<Operation>emptyList(),
					links, groups);
			return accept(operations, visitor);
		}
	}

	static <T> Optional<T> accept(final Iterable<? extends Operation> operations, final OperationVisitor<T> visitor) {
		for (Operation operation : operations) {
			Optional<T> result = operation.accept(visitor);
			if (result.isPresent()) {
				return result;
			}
		}
		return Optional.absent();
	}

	@Nullable
	public OperationHeader getHeader() {
		return header;
	}

	public Iterable<OperationLink> getLinks() {
		return links;
	}

	public Iterable<OperationGroup> getGroups() {
		return groups;
	}

	@Nullable
	public Integer getWeight() {
		return weight;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, header, links, groups, weight);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final OperationGroup other = (OperationGroup) obj;
		return Objects.equal(this.id, other.id)
				&& Objects.equal(this.header, other.header)
				&& Iterables.elementsEqual(this.links, other.links)
				&& Iterables.elementsEqual(this.groups, other.groups)
				&& Objects.equal(this.weight, other.weight);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("header", header)
				.add("links", Iterables.toString(links))
				.add("groups", Iterables.toString(groups))
				.add("weight", weight)
				.toString();
	}
}
