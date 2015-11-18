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

import java.net.URI;

/**
 * Any resource which is addressable (has "self" URI) and has a name.
 *
 * @since v0.1
 */
public class AddressableNamedEntity implements AddressableEntity, NamedEntity {
	protected final URI self;
	protected final String name;

	public AddressableNamedEntity(URI self, String name) {
		this.name = name;
		this.self = self;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getToStringHelper().toString();
	}

	protected Objects.ToStringHelper getToStringHelper() {
		return Objects.toStringHelper(this).
				add("self", self).
				add("name", name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AddressableNamedEntity) {
			AddressableNamedEntity that = (AddressableNamedEntity) obj;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.name, that.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(self, name);
	}
}
