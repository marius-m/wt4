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
import com.atlassian.jira.rest.client.api.NamedEntity;
import com.google.common.base.Objects;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * Basic information about a project component
 *
 * @since v0.1
 */
public class BasicComponent implements AddressableEntity, NamedEntity {
	@Nullable
	private final Long id;
	private final URI self;
	private final String name;
	@Nullable
	private final String description;

	public BasicComponent(URI self, @Nullable Long id, String name, @Nullable String description) {
		this.self = self;
		this.id = id;
		this.name = name;
		this.description = description;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	public String getName() {
		return name;
	}

	@Nullable
	public Long getId() {
		return id;
	}

	/**
	 * @return optional description for this project (as defined by the project admin)
	 */
	@Nullable
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("id", id).
				add("self", self).
				add("name", name).
				add("description", description).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicComponent) {
			BasicComponent that = (BasicComponent) obj;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.id, that.id)
					&& Objects.equal(this.name, that.name)
					&& Objects.equal(this.description, that.description);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(self, name, description);
	}

}
