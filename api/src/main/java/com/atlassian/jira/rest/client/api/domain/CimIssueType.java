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

package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.google.common.base.Objects;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Map;

/**
 * Describes issue type with fields info map.<br/>
 * The CIM prefix stands for CreateIssueMetadata as this class is used in output of {@link IssueRestClient#getCreateIssueMetadata(GetCreateIssueMetadataOptions)}
 *
 * @since v1.0
 */
public class CimIssueType extends IssueType {

	private final Map<String, CimFieldInfo> fields;

	public CimIssueType(URI self, @Nullable Long id, String name, boolean isSubtask, String description, URI iconUri, Map<String, CimFieldInfo> fields) {
		super(self, id, name, isSubtask, description, iconUri);
		this.fields = fields;
	}

	public Map<String, CimFieldInfo> getFields() {
		return fields;
	}

	@Nullable
	public CimFieldInfo getField(IssueFieldId fieldId) {
		return fields.get(fieldId.id);
	}

	/**
	 * Returns ToStringHelper with all fields inserted. Override this method to insert additional fields.
	 *
	 * @return ToStringHelper
	 */
	@Override
	protected Objects.ToStringHelper getToStringHelper() {
		return super.getToStringHelper().add("fields", fields);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CimIssueType) {
			CimIssueType that = (CimIssueType) obj;
			return super.equals(obj)
					&& Objects.equal(this.fields, that.fields);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), fields);
	}
}
