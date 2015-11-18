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
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * A JIRA comment
 *
 * @since v0.1
 */
public class Comment implements AddressableEntity {
	private final URI self;
	@Nullable
	private final Long id;
	@Nullable
	private final BasicUser author;
	@Nullable
	private final BasicUser updateAuthor;
	private final DateTime creationDate;
	private final DateTime updateDate;
	private final String body;
	@Nullable
	private final Visibility visibility;

	public Comment(URI self, String body, @Nullable BasicUser author, @Nullable BasicUser updateAuthor, DateTime creationDate, DateTime updateDate, Visibility visibility, @Nullable Long id) {
		this.author = author;
		this.updateAuthor = updateAuthor;
		this.creationDate = creationDate;
		this.updateDate = updateDate;
		this.body = body;
		this.self = self;
		this.visibility = visibility;
		this.id = id;
	}

	public static Comment valueOf(String body) {
		return new Comment(null, body, null, null, null, null, null, null);
	}

	public static Comment createWithRoleLevel(String body, String roleLevel) {
		return new Comment(null, body, null, null, null, null, Visibility.role(roleLevel), null);
	}

	public static Comment createWithGroupLevel(String body, String groupLevel) {
		return new Comment(null, body, null, null, null, null, Visibility.group(groupLevel), null);
	}

	public boolean wasUpdated() {
		return updateDate.isAfter(creationDate);
	}

	public String getBody() {
		return body;
	}

	@Nullable
	public Long getId() {
		return id;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	@Nullable
	public BasicUser getAuthor() {
		return author;
	}

	@Nullable
	public BasicUser getUpdateAuthor() {
		return updateAuthor;
	}

	public DateTime getCreationDate() {
		return creationDate;
	}

	public DateTime getUpdateDate() {
		return updateDate;
	}

	@Nullable
	public Visibility getVisibility() {
		return visibility;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("self", self)
				.add("id", id)
				.add("body", body)
				.add("author", author)
				.add("updateAuthor", updateAuthor)
				.add("creationDate", creationDate)
				.add("visibility", visibility)
				.add("updateDate", updateDate).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Comment) {
			Comment that = (Comment) obj;
			return Objects.equal(this.self, that.self)
					&& Objects.equal(this.id, that.id)
					&& Objects.equal(this.body, that.body)
					&& Objects.equal(this.author, that.author)
					&& Objects.equal(this.updateAuthor, that.updateAuthor)
					&& Objects.equal(this.creationDate, that.creationDate)
					&& Objects.equal(this.updateDate, that.updateDate)
					&& Objects.equal(this.visibility, that.visibility)
					&& Objects.equal(this.body, that.body);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(self, id, body, author, updateAuthor, creationDate, updateDate, visibility);
	}

}
