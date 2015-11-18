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

package com.atlassian.jira.rest.client.api.domain.input;

import com.google.common.base.Objects;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

/**
 * Input data describing details of a project version to create.
 *
 * @since v4.4
 */
public class VersionInput {
	private final String projectKey;
	private final String name;
	@Nullable
	private final String description;
	private final DateTime releaseDate;
	private boolean isArchived;
	private boolean isReleased;

	public VersionInput(String projectKey, String name, @Nullable String description, @Nullable DateTime releaseDate,
			boolean isArchived, boolean isReleased) {
		this.projectKey = projectKey;
		this.name = name;
		this.description = description;
		this.releaseDate = releaseDate;
		this.isArchived = isArchived;
		this.isReleased = isReleased;
	}

	public static VersionInput create(String projectKey, String name, @Nullable String description, @Nullable DateTime releaseDate,
			boolean archived, boolean release) {
		return new VersionInput(projectKey, name, description, releaseDate, archived, release);
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getName() {
		return name;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	public DateTime getReleaseDate() {
		return releaseDate;
	}

	public boolean isArchived() {
		return isArchived;
	}

	public boolean isReleased() {
		return isReleased;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("name", name)
				.add("projectKey", projectKey)
				.add("description", description)
				.add("releaseDate", releaseDate)
				.add("isArchived", isArchived)
				.add("isReleased", isReleased)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VersionInput) {
			VersionInput that = (VersionInput) obj;
			return Objects.equal(this.projectKey, that.projectKey)
					&& Objects.equal(this.name, that.name)
					&& Objects.equal(this.releaseDate, that.releaseDate)
					&& Objects.equal(this.isArchived, that.isArchived)
					&& Objects.equal(this.isReleased, that.isReleased);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name, projectKey, description, releaseDate, isArchived, isReleased);
	}


}
