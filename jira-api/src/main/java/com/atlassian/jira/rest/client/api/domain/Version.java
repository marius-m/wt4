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
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * Complete information about a version defined for a JIRA project
 *
 * @since v0.1
 */
public class Version implements AddressableEntity, NamedEntity {
	private final URI self;
	@Nullable
	private final Long id;
	private final String description;
	private final String name;
	private final boolean isArchived;
	private final boolean isReleased;
	@Nullable
	private final DateTime releaseDate;

	public Version(URI self, @Nullable Long id, String name, String description, boolean archived, boolean released, @Nullable DateTime releaseDate) {
		this.self = self;
		this.id = id;
		this.description = description;
		this.name = name;
		isArchived = archived;
		isReleased = released;
		this.releaseDate = releaseDate;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	@Nullable
	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public boolean isArchived() {
		return isArchived;
	}

	public boolean isReleased() {
		return isReleased;
	}

	@Nullable
	public DateTime getReleaseDate() {
		return releaseDate;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("self", self).
				add("id", id).
				add("name", name).
				add("description", description).
				add("isArchived", isArchived).
				add("isReleased", isReleased).
				add("releaseDate", releaseDate).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Version) {
			Version that = (Version) obj;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.id, that.id)
					&& Objects.equal(this.name, that.name)
					&& Objects.equal(this.description, that.description)
					&& Objects.equal(this.isArchived, that.isArchived)
					&& Objects.equal(this.isReleased, that.isReleased)
					&& Objects.equal(this.releaseDate, that.releaseDate);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(self, id, name, description, isArchived, isReleased, releaseDate);
	}

}
