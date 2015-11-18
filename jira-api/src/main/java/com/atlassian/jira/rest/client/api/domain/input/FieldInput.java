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

package com.atlassian.jira.rest.client.api.domain.input;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.IdentifiableEntity;
import com.google.common.base.Objects;

/**
 * New value for selected field - used while changing issue fields - e.g. while transitioning issue.
 *
 * @since v0.1
 */
public class FieldInput implements IdentifiableEntity<String> {
	private final String id;
	private final Object value;

	/**
	 * @param id    field id
	 * @param value new value for this issue field
	 */
	public FieldInput(String id, Object value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * @param field issue field
	 * @param value new value for this issue field
	 */
	public FieldInput(IssueFieldId field, Object value) {
		this.id = field.id;
		this.value = value;
	}

	/**
	 * @return field id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return new value for this issue field
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FieldInput) {
			final FieldInput other = (FieldInput) obj;
			return Objects.equal(this.id, other.id)
					&& Objects.equal(this.value, other.value);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("value", value)
				.toString();
	}
}
