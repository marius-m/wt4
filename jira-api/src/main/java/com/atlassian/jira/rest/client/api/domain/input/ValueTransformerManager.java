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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class allows to register {@link ValueTransformer} objects and then perform value transformation using
 * registered transformers by invoking {@link ValueTransformerManager#apply(Object)}.<br/>
 *
 * @since v1.0
 */
public class ValueTransformerManager implements Function<Object, Object> {
	public final List<ValueTransformer> valueTransformers = Lists.newArrayList();

	public ValueTransformerManager() {
	}

	/**
	 * Registers new transformer at the end of list so it will be processed after existing transformers.
	 *
	 * @param transformer Transformer to register
	 * @return this
	 */
	public ValueTransformerManager registerTransformer(final ValueTransformer transformer) {
		valueTransformers.add(transformer);
		return this;
	}

	/**
	 * Registers new transformer at the beginning of list so it will be processed before existing transformers.
	 *
	 * @param transformer Transformer to register
	 * @return this
	 */
	@SuppressWarnings("unused")
	public ValueTransformerManager registerTransformerAsFirst(final ValueTransformer transformer) {
		valueTransformers.add(0, transformer);
		return this;
	}

	/**
	 * Use registered transformers to transform given value.
	 *
	 * @param rawInput Value to transform
	 * @return transformed value
	 * @throws CannotTransformValueException when any of available transformers was able to transform given value
	 */
	public Object apply(@Nullable Object rawInput) {
		if (rawInput instanceof Iterable) {
			@SuppressWarnings("unchecked")
			final Iterable<Object> rawInputObjects = (Iterable<Object>) rawInput;
			return ImmutableList.copyOf(Iterables.transform(rawInputObjects, this));
		}

		for (ValueTransformer valueTransformer : valueTransformers) {
			final Object transformedValue = valueTransformer.apply(rawInput);
			if (!ValueTransformer.CANNOT_HANDLE.equals(transformedValue)) {
				return transformedValue;
			}
		}

		throw new CannotTransformValueException("Any of available transformers was able to transform given value. Value is: "
				+ (rawInput == null ? "NULL" : rawInput.getClass().getName() + ": " + rawInput.toString()));
	}
}
