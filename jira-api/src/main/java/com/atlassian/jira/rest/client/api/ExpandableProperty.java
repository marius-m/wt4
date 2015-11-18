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

package com.atlassian.jira.rest.client.api;

import com.google.common.base.Objects;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Represents a resource which can be expandable - that is REST API is capable of sending just the number
 * of child resources or when the entity is expanded, also the child resources themselves
 *
 * @since v0.1
 */
public class ExpandableProperty<T> {
	private final int size;

	public ExpandableProperty(int size) {
		this.size = size;
		items = null;
	}

	public ExpandableProperty(int size, @Nullable Collection<T> items) {
		this.size = size;
		this.items = items;
	}

	public ExpandableProperty(Collection<T> items) {
		this.size = items.size();
		this.items = items;
	}

	public int getSize() {
		return size;
	}

	@Nullable
	final private Collection<T> items;

	@Nullable
	public Iterable<T> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("size", size).
				add("items", items).
				toString();
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExpandableProperty) {
			ExpandableProperty that = (ExpandableProperty) obj;
			return Objects.equal(this.size, that.size)
					&& Objects.equal(this.items, that.items);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(size, items);
	}
}
