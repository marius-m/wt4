/*
 * Copyright (C) 2014 Atlassian
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

import com.google.common.base.Optional;

import javax.annotation.Nullable;

public interface Operation {
	/**
	 * @return Operation id. May be null.
	 */
	@Nullable String getId();

	/**
	 * Traverse through operation elements to visit them. Traversal will stop on first non absent value
	 * returned from the visitor.
	 *
	 * @param visitor Visitor to visit operation element
	 * @param <T> Visiting result type
	 * @return Value returned from the visitor.
	 */
	<T> Optional<T> accept(OperationVisitor<T> visitor);

}
