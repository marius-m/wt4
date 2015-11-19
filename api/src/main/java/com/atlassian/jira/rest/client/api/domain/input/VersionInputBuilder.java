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

import com.atlassian.jira.rest.client.api.domain.Version;
import org.joda.time.DateTime;

public class VersionInputBuilder {
	private final String projectKey;
	private String name;
	private String description;
	private DateTime releaseDate;
	private boolean archived;
	private boolean released;

	public VersionInputBuilder(String projectKey) {
		this.projectKey = projectKey;
	}

	public VersionInputBuilder(String projectKey, Version version) {
		this(projectKey);
		this.name = version.getName();
		this.description = version.getDescription();
		this.archived = version.isArchived();
		this.released = version.isReleased();
		this.releaseDate = version.getReleaseDate();
	}


	public VersionInputBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public VersionInputBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public VersionInputBuilder setReleaseDate(DateTime releaseDate) {
		this.releaseDate = releaseDate;
		return this;
	}

	public VersionInputBuilder setArchived(boolean archived) {
		this.archived = archived;
		return this;
	}

	public VersionInputBuilder setReleased(boolean released) {
		this.released = released;
		return this;
	}

	public VersionInput build() {
		return new VersionInput(projectKey, name, description, releaseDate, archived, released);
	}
}