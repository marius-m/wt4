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
 * Basic information about a JIRA project
 *
 * @since v0.1
 */
public class BasicProject implements AddressableEntity, NamedEntity {
	private final URI self;
	private final String key;
	@Nullable
	private final Long id;
	@Nullable
	private final String name;

	public BasicProject(final URI self, final String key, @Nullable final Long id, final @Nullable String name) {
		this.self = self;
		this.key = key;
		this.id = id;
		this.name = name;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	public String getKey() {
		return key;
	}

	@Nullable
	public String getName() {
		return name;
	}

	@Nullable
	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return getToStringHelper().toString();
	}

	protected Objects.ToStringHelper getToStringHelper() {
		return Objects.toStringHelper(this).
				add("self", self).
				add("key", key).
				add("id", id).
				add("name", name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicProject) {
			BasicProject that = (BasicProject) obj;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.name, that.name)
					&& Objects.equal(this.id, that.id)
					&& Objects.equal(this.key, that.key);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(self, name, id, key);
	}

}
