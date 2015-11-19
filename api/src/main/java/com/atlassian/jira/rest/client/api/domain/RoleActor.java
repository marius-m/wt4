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

import com.atlassian.jira.rest.client.api.NamedEntity;
import com.google.common.base.Objects;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * Association between users and project roles.
 *
 * @since 1.0
 */
public class RoleActor implements NamedEntity {

	/**
	 * Type of a role actor which associates a project with some particular user.
	 */
	@SuppressWarnings("UnusedDeclaration")
	private static final String TYPE_ATLASSIAN_USER_ROLE = "atlassian-user-role-actor";


	/**
	 * Type of a role actor which associates a project with a group of users, for instance: administrators, developers.
	 */
	@SuppressWarnings("UnusedDeclaration")
	private static final String TYPE_ATLASSIAN_GROUP_ROLE = "atlassian-group-role-actor";

	private final Long id;
	private final String displayName;
	private final String type;
	private final String name;
	private final URI avatarUrl;

	public RoleActor(Long id, String displayName, String type, String name, @Nullable URI avatarUrl) {
		this.id = id;
		this.displayName = displayName;
		this.type = type;
		this.name = name;
		this.avatarUrl = avatarUrl;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return the viewable name of this role actor.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return string identifying the implementation type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return an URI of the avatar of this role actor.
	 */
	public URI getAvatarUri() {
		return avatarUrl;
	}

	/**
	 * @return the unique identifier for this role actor.
	 */
	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RoleActor) {
			RoleActor that = (RoleActor) o;
			return Objects.equal(this.getName(), that.getName())
					&& Objects.equal(this.id, that.getId())
					&& Objects.equal(this.getAvatarUri(), that.getAvatarUri())
					&& Objects.equal(this.getType(), that.getType())
					&& Objects.equal(this.getDisplayName(), that.getDisplayName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), name, avatarUrl, type, displayName);
	}

	@Override
	public String toString() {
		return getToStringHelper().toString();
	}

	protected Objects.ToStringHelper getToStringHelper() {
		return Objects.toStringHelper(this)
				.add("displayName", displayName)
				.add("type", type)
				.add("name", name)
				.add("avatarUrl", avatarUrl);
	}
}
