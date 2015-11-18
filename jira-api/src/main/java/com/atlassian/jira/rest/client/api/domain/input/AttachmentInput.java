/*
 * Copyright (C) 2011 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.api.domain.input;

import java.io.InputStream;

/**
 * Data required to add an attachment
 *
 * @since v0.2
 */
public class AttachmentInput {
	private final InputStream in;
	private final String filename;

	public AttachmentInput(String filename, InputStream in) {
		this.filename = filename;
		this.in = in;
	}

	public InputStream getInputStream() {
		return in;
	}

	public String getFilename() {
		return filename;
	}
}
