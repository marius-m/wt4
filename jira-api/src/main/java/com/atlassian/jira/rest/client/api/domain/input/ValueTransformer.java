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

package com.atlassian.jira.rest.client.api.domain.input;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Implement this interface if you want to provide your own value transformer to ValueTransformerManager.<br/>
 * You should return {@link ValueTransformer#CANNOT_HANDLE} in {@link ValueTransformer#apply(Object)} when implemented
 * transformer cannot transform given value.<br/>
 *
 * @since v1.0
 */
public interface ValueTransformer extends Function<Object, Object> {
	public static final Object CANNOT_HANDLE = new Object();

	/**
	 * Transform given object into other representation, that can be used as input for field value generators.
	 *
	 * @param from Source object.
	 * @return The resulting object or {@link ValueTransformer#CANNOT_HANDLE} when given value cannot be transformed.
	 */
	@Override
	Object apply(@Nullable Object from);
}
