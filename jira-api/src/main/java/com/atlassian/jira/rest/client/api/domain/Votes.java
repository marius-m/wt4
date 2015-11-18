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

import java.net.URI;
import java.util.Collection;

import static com.google.common.base.Objects.ToStringHelper;

/**
 * Complete information about the voters for given issue
 *
 * @since v0.1
 */
public class Votes extends BasicVotes {
	private final Collection<BasicUser> users;

	public Votes(URI self, int votes, boolean hasVoted, Collection<BasicUser> users) {
		super(self, votes, hasVoted);
		this.users = users;
	}

	public Iterable<BasicUser> getUsers() {
		return users;
	}

	@Override
	protected ToStringHelper getToStringHelper() {
		return super.getToStringHelper().add("users", users);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Votes) {
			final Votes that = (Votes) obj;
			return super.equals(that) && Objects.equal(this.users, that.users);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), this.users);
	}

}
