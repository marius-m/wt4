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

import com.atlassian.jira.rest.client.api.IdentifiableEntity;
import com.atlassian.jira.rest.client.api.NamedEntity;
import com.google.common.base.Objects;

/**
 * JIRA issue field with its current value.
 *
 * @since v0.1
 */
public class IssueField implements NamedEntity, IdentifiableEntity<String> {

	private final String id;
	private final String name;
	private final String type;
	private final Object value;

	public IssueField(String id, String name, String type, Object value) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("id", id).
				add("name", name).
				add("type", type).
				add("value", getValue()).
				toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, name, type); // for the sake of performance we don't include "value" field here
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IssueField) {
			final IssueField that = (IssueField) obj;
			return Objects.equal(this.id, that.id)
					&& Objects.equal(this.name, that.name)
					&& Objects.equal(this.type, that.type)
					&& Objects.equal(this.value, that.value);
		}
		return false;
	}

}
