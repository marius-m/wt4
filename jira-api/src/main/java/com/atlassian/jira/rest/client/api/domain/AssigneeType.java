/*
 * Copyright (C) 2011 Atlassian
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

/**
 * Possible assignee types for project components
 *
 * @since com.atlassian.jira.rest.client.api 0.3, server 4.4
 */
public enum AssigneeType {
	PROJECT_DEFAULT,
	COMPONENT_LEAD,
	PROJECT_LEAD,
	UNASSIGNED
}
