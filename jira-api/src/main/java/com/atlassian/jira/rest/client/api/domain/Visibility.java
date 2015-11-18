/*
 * Copyright (C) 2011 Atlassian
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

/**
 * Represents visibility (access level) of selected element (comment, worklog, etc.)
 *
 * @since v0.2
 */
public class Visibility {
	public enum Type {
		ROLE, GROUP
	}

	private final Type type;
	private final String value;

	public Visibility(Type type, String value) {
		this.type = type;
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public static Visibility role(String value) {
		return new Visibility(Type.ROLE, value);
	}

	public static Visibility group(String group) {
		return new Visibility(Type.GROUP, group);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("type", type).
				add("value", value).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Visibility) {
			Visibility that = (Visibility) obj;
			return Objects.equal(this.type, that.type) && Objects.equal(this.value, that.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type, value);
	}
}
