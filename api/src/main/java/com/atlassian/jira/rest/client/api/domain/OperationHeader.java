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

import javax.annotation.Nullable;

/**
 * Represents operations header
 *
 * @since 2.0
 */
public class OperationHeader implements Operation {
	@Nullable private final String id;
	private final String label;
	@Nullable private final String title;
	@Nullable private final String iconClass;

	public OperationHeader(@Nullable final String id, final String label, @Nullable final String title, @Nullable final String iconClass) {
		this.id = id;
		this.label = label;
		this.title = title;
		this.iconClass = iconClass;
	}

	@Nullable
	@Override
	public String getId() {
		return id;
	}

	@Override
	public <T> Optional<T> accept(final OperationVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getLabel() {
		return label;
	}

	@Nullable
	public String getTitle() {
		return title;
	}

	@Nullable
	public String getIconClass() {
		return iconClass;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("label", label)
				.add("title", title)
				.add("iconClass", iconClass)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof OperationHeader) {
			OperationHeader that = (OperationHeader) o;
			return Objects.equal(id, that.id)
					&& Objects.equal(label, that.label)
					&& Objects.equal(title, that.title)
					&& Objects.equal(iconClass, that.iconClass);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, label, title, iconClass);
	}
}
