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

import com.google.common.base.Objects;

import javax.annotation.Nullable;

/**
 * Represents schema of field in JIRA
 *
 * @since v1.0
 */
public class FieldSchema {

	private final String type;
	@Nullable
	private final String items;
	@Nullable
	private final String system;
	@Nullable
	private final String custom;
	@Nullable
	private final Long customId;

	public FieldSchema(String type, String items, String system, String custom, Long customId) {
		this.type = type;
		this.items = items;
		this.system = system;
		this.custom = custom;
		this.customId = customId;
	}

	public String getType() {
		return type;
	}

	@Nullable
	public String getItems() {
		return items;
	}

	@Nullable
	public String getSystem() {
		return system;
	}

	@Nullable
	public String getCustom() {
		return custom;
	}

	@Nullable
	public Long getCustomId() {
		return customId;
	}

	public boolean isCustom() {
		return custom != null;
	}

	/**
	 * Returns ToStringHelper with all fields inserted. Override this method to insert additional fields.
	 *
	 * @return ToStringHelper
	 */
	protected Objects.ToStringHelper getToStringHelper() {
		return Objects.toStringHelper(this).
				add("type", type).
				add("items", items).
				add("system", system).
				add("custom", custom).
				add("customId", customId);
	}

	@Override
	public String toString() {
		return getToStringHelper().toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FieldSchema) {
			FieldSchema that = (FieldSchema) obj;
			return Objects.equal(this.type, that.type)
					&& Objects.equal(this.items, that.items)
					&& Objects.equal(this.system, that.system)
					&& Objects.equal(this.custom, that.custom)
					&& Objects.equal(this.customId, that.customId);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type, items, system, custom, customId);
	}
}
