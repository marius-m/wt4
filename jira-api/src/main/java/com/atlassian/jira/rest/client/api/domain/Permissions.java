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

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Map;

public class Permissions {
	/**
	 * Permission key for ability to log work done against an issue. Only useful if Time Tracking is turned on.
	 */
	public static final String WORK_ISSUE = "WORK_ISSUE";
	private final Map<String, Permission> permissionMap;

	public Permissions(final Iterable<Permission> permissions) {
		this.permissionMap = Maps.uniqueIndex(permissions, Permission.TO_KEY);
	}

	public Map<String, Permission> getPermissionMap() {
		return permissionMap;
	}

	public boolean havePermission(final String permissionKey) {
		final Permission permission = getPermission(permissionKey);
		return (permission != null && permission.havePermission());
	}

	@Nullable
	public Permission getPermission(final String permissionKey) {
		return permissionMap.get(permissionKey);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("permissionMap", permissionMap)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Permissions) {
			Permissions that = (Permissions) o;
			return Objects.equal(permissionMap, that.permissionMap);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return permissionMap.hashCode();
	}
}
