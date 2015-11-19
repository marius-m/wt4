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

package com.atlassian.jira.rest.client.api.domain.input;

import com.atlassian.jira.rest.client.api.domain.Comment;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * Input data used while transitioning an issue including new values for this issue and the optional comment.
 *
 * @since v0.1
 */
public class TransitionInput {
	private final int id;
	@Nullable
	private final Comment comment;

	private final Collection<FieldInput> fields;

	/**
	 * @param id     id of the issue transition which should be performed
	 * @param fields new values for the issue fields. Use empty collection if no fields are to be changed
	 */
	public TransitionInput(int id, Collection<FieldInput> fields) {
		this(id, fields, null);
	}


	/**
	 * @param id      id of the issue transition which should be performed
	 * @param fields  new values for the issue fields. Use empty collection if no fields are to be changed
	 * @param comment optional comment
	 */
	public TransitionInput(int id, Collection<FieldInput> fields, @Nullable Comment comment) {
		this.id = id;
		this.comment = comment;
		this.fields = fields;
	}

	/**
	 * @param id      id of the issue transition which should be performed
	 * @param comment optional comment
	 */
	public TransitionInput(int id, @Nullable Comment comment) {
		this(id, Collections.<FieldInput>emptyList(), comment);
	}

	public TransitionInput(int id) {
		this(id, Collections.<FieldInput>emptyList(), null);
	}

	/**
	 * @return id of the issue transition which should be performed
	 */
	public int getId() {
		return id;
	}

	@Nullable
	public Comment getComment() {
		return comment;
	}

	public Iterable<FieldInput> getFields() {
		return fields;
	}
}
