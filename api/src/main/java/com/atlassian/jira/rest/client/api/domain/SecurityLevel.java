/*
 * Copyright (C) 2013 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.IdentifiableEntity;
import com.google.common.base.Objects;

import java.net.URI;

/**
 * Represents JIRA Security Level
 *
 * @since v2.0
 */
public class SecurityLevel extends AddressableNamedEntity implements IdentifiableEntity<Long> {

	private final Long id;
	private final String description;

	public SecurityLevel(final URI self, final Long id, final String name, final String description) {
		super(self, name);
		this.id = id;
		this.description = description;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), id, description);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SecurityLevel) {
			final SecurityLevel that = (SecurityLevel) obj;
			return super.equals(that)
					&& Objects.equal(this.id, that.id)
					&& Objects.equal(this.description, that.description);
		}
		return false;
	}

	@Override
	protected Objects.ToStringHelper getToStringHelper() {
		return super.getToStringHelper()
				.add("id", id)
				.add("description", description);
	}
}
