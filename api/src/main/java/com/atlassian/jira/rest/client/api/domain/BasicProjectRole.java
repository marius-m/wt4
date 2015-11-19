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
package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.AddressableEntity;
import com.atlassian.jira.rest.client.api.NamedEntity;
import com.google.common.base.Objects;

import java.net.URI;

/**
 * Basic information about a JIRA project's role.
 */
public class BasicProjectRole implements AddressableEntity, NamedEntity {

	private final URI self;
	private final String name;

	public BasicProjectRole(URI self, String name) {
		this.self = self;
		this.name = name;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	/**
	 * @return the name of this project role.
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BasicProjectRole) {
			final BasicProjectRole that = (BasicProjectRole) o;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.name, that.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), self, name);
	}

	@Override
	public String toString() {
		return getToStringHelper().toString();
	}

	protected Objects.ToStringHelper getToStringHelper() {
		return Objects.toStringHelper(this)
				.add("self", self)
				.add("name", name);
	}
}
