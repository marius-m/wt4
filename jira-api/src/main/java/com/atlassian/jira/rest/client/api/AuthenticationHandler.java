/*
 * Copyright (C) 2010-2012 Atlassian
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

import com.atlassian.httpclient.api.Request;

/**
 * Interface for classes which authenticate the requests. The configure method is invoked during each request.
 * For instance, the implementation of this handling which would configure the request to do a basic http authentication would
 * have a following implementation.
 * <p/>
 * public void configure(final Request request) {
 * request.setHeader("Authorization", "Basic " + base64EncodedCredentials());
 * }
 *
 * @since v0.1
 */
public interface AuthenticationHandler {

	void configure(final Request request);

}
