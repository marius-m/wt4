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
 * Basic information about watchers of a JIRA issue
 *
 * @since v0.1
 */
public class BasicWatchers implements AddressableEntity {
	private final URI self;
	private final boolean isWatching;
	private final int numWatchers;

	public BasicWatchers(URI self, boolean watching, int numWatchers) {
		this.self = self;
		isWatching = watching;
		this.numWatchers = numWatchers;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	public boolean isWatching() {
		return isWatching;
	}

	public int getNumWatchers() {
		return numWatchers;
	}

	protected Objects.ToStringHelper getToStringHelper() {
		return Objects.toStringHelper(this).
				add("self", self).
				add("isWatching", isWatching).
				add("numWatchers", numWatchers);
	}

	@Override
	public String toString() {
		return getToStringHelper().toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicWatchers) {
			final BasicWatchers that = (BasicWatchers) obj;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.isWatching, that.isWatching)
					&& Objects.equal(this.numWatchers, that.numWatchers);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(self, isWatching, numWatchers);
	}
}
