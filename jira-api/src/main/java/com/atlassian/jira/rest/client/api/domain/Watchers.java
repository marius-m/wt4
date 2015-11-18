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

import com.google.common.base.Objects;

import java.util.Collection;

/**
 * Complete information about the watchers of given issue
 *
 * @since v0.1
 */
public class Watchers extends BasicWatchers {
	private final Collection<BasicUser> users;

	public Watchers(BasicWatchers basicWatchers, Collection<BasicUser> users) {
		super(basicWatchers.getSelf(), basicWatchers.isWatching(), basicWatchers.getNumWatchers());
		this.users = users;
	}

	public Iterable<BasicUser> getUsers() {
		return users;
	}

	@Override
	protected Objects.ToStringHelper getToStringHelper() {
		return super.getToStringHelper().add("watchers", users);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), users);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Watchers) {
			final Watchers that = (Watchers) obj;
			return super.equals(that) && Objects.equal(this.users, that.users);
		}
		return false;
	}
}
