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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Represents object with fields in IssueInputField's value.
 *
 * @since v1.0
 */
public class ComplexIssueInputFieldValue {

	private final Map<String, Object> valuesMap;

	public static ComplexIssueInputFieldValue with(String key, Object value) {
		return new ComplexIssueInputFieldValue(ImmutableMap.<String, Object>of(key, value));
	}

	public ComplexIssueInputFieldValue(Map<String, Object> valuesMap) {
		this.valuesMap = valuesMap;
	}

	public Map<String, Object> getValuesMap() {
		return valuesMap;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("valuesMap", valuesMap)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(valuesMap);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ComplexIssueInputFieldValue) {
			final ComplexIssueInputFieldValue other = (ComplexIssueInputFieldValue) obj;
			return Objects.equal(this.valuesMap, other.valuesMap);
		}
		return false;
	}
}
