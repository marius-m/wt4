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

package com.atlassian.jira.rest.client.api.domain.input;

/**
 * Defines a new position for a project version (while moving it) by {@link com.atlassian.jira.rest.client.api.VersionRestClient#moveVersion(java.net.URI, VersionPosition)}
 *
 * @since 0.3 com.atlassian.jira.rest.client.api, 4.4 server
 */
public enum VersionPosition {
	FIRST, LAST, EARLIER, LATER
}
