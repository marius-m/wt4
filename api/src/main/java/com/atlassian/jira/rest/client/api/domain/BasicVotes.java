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
import com.google.common.base.Objects;

import java.net.URI;

/**
 * Basic information about voters of a JIRA issue
 *
 * @since v0.1
 */
public class BasicVotes implements AddressableEntity {
	private final URI self;
	private final int votes;
	private final boolean hasVoted;

	public BasicVotes(URI self, int votes, boolean hasVoted) {
		this.self = self;
		this.votes = votes;
		this.hasVoted = hasVoted;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	public int getVotes() {
		return votes;
	}

	public boolean hasVoted() {
		return hasVoted;
	}

	protected Objects.ToStringHelper getToStringHelper() {
		return Objects.toStringHelper(this).
				add("self", self).
				add("votes", votes).
				add("hasVoted", hasVoted);
	}

	@Override
	public String toString() {
		return getToStringHelper().toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicVotes) {
			final BasicVotes that = (BasicVotes) obj;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.votes, that.votes)
					&& Objects.equal(this.hasVoted, that.hasVoted);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(self, votes, hasVoted);
	}
}
