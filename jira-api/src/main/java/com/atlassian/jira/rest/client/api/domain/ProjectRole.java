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

import com.atlassian.jira.rest.client.api.IdentifiableEntity;
import com.google.common.base.Objects;

import java.net.URI;
import java.util.Collection;

/**
 * A way to group users (@see RoleActors) with projects. An example would be a global role called "testers". If you
 * have a project X and a project Y, you would then be able to configure different RoleActors in the "testers" role
 * for project X than for project Y. You can use ProjectRole objects as the target of Notification and Permission
 * schemes.
 *
 * @see com.atlassian.jira.security.roles.ProjectRole
 */
@SuppressWarnings("JavadocReference")
public class ProjectRole extends BasicProjectRole implements IdentifiableEntity<Long> {

	private final String description;
	private final Collection<RoleActor> actors;
	private final long id;

	public ProjectRole(long id, URI self, String name, String description, Collection<RoleActor> actors) {
		super(self, name);
		this.id = id;
		this.description = description;
		this.actors = actors;
	}

	/**
	 * @return description of this project role.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return actors associated with this role.
	 */
	public Iterable<RoleActor> getActors() {
		return actors;
	}

	/**
	 * @return the unique id for this project role.
	 */
	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProjectRole) {
			final ProjectRole that = (ProjectRole) o;
			return super.equals(o)
					&& Objects.equal(this.description, that.description)
					&& Objects.equal(this.actors, that.actors)
					&& Objects.equal(this.id, that.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), description, actors);
	}

	@Override
	protected Objects.ToStringHelper getToStringHelper() {
		return super.getToStringHelper()
				.add("description", description)
				.add("actors", actors);
	}
}
