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
package com.atlassian.jira.rest.client.api;

import com.atlassian.jira.rest.client.api.domain.Permissions;
import com.atlassian.jira.rest.client.api.domain.input.MyPermissionsInput;
import com.atlassian.util.concurrent.Promise;

import javax.annotation.Nullable;

public interface MyPermissionsRestClient {
	/**
	 * Returns permissions for current user and context defined by {@code permissionInput}
	 * @param permissionInput Permissions context ie. projectKey OR projectId OR issueKey OR issueId.
	 *                        <ul>
	 *                        <li>When no context supplied (null) the project related permissions will return true
	 *                        if the user has that permission in ANY project</li>
	 *                        <li>If a project context is provided, project related permissions will return true
	 *                        if the user has the permissions in the specified project. For permissions
	 *                        that are determined using issue data (e.g Current Assignee), true will be returned
	 *                        if the user meets the permission criteria in ANY issue in that project</li>
	 *                        <li>If an issue context is provided, it will return whether or not the user
	 *                        has each permission in that specific issue</li>
	 *                        </ul>
	 * @return Permissions for user in the context
	 */
	Promise<Permissions> getMyPermissions(@Nullable MyPermissionsInput permissionInput);
}
