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

package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.AddressableEntity;
import com.atlassian.jira.rest.client.api.IdentifiableEntity;
import com.atlassian.jira.rest.client.api.NamedEntity;
import com.google.common.base.Objects;

import java.net.URI;

/**
 * Complete information about a single issue type defined in JIRA
 *
 * @since v0.1
 */
public class IssueType implements AddressableEntity, NamedEntity, IdentifiableEntity<Long> {
	private final URI self;
	private final Long id;
	private final String name;
	private final boolean isSubtask;
	private final String description;
	private final URI iconUri;

	public IssueType(URI self, Long id, String name, boolean isSubtask, String description, URI iconUri) {
		this.self = self;
		this.id = id;
		this.name = name;
		this.isSubtask = isSubtask;
		this.description = description;
		this.iconUri = iconUri;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean isSubtask() {
		return isSubtask;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	public String getDescription() {
		return description;
	}

	public URI getIconUri() {
		return iconUri;
	}

	protected Objects.ToStringHelper getToStringHelper() {
		return Objects.toStringHelper(this)
				.add("self", self)
				.add("id", id)
				.add("name", name)
				.add("isSubtask", isSubtask)
				.add("description", description)
				.add("iconUri", iconUri);
	}

	@Override
	public String toString() {
		return getToStringHelper().toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IssueType) {
			IssueType that = (IssueType) obj;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.id, that.id)
					&& Objects.equal(this.name, that.name)
					&& Objects.equal(this.isSubtask, that.isSubtask)
					&& Objects.equal(this.description, that.description)
					&& Objects.equal(this.iconUri, that.iconUri);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(self, id, name, isSubtask, description, iconUri);
	}

}
