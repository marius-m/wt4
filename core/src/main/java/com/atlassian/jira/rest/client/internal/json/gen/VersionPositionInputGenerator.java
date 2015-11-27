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

package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.input.VersionPosition;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class VersionPositionInputGenerator implements JsonGenerator<VersionPosition> {
	@Override
	public JSONObject generate(VersionPosition versionPosition) throws JSONException {
		final JSONObject res = new JSONObject();
		final String posValue;
		switch (versionPosition) {
			case FIRST:
				posValue = "First";
				break;
			case LAST:
				posValue = "Last";
				break;
			case EARLIER:
				posValue = "Earlier";
				break;
			case LATER:
				posValue = "Later";
				break;
			default:
				throw new RestClientException("Unsupported position [" + versionPosition + "]", null);
		}
		res.put("position", posValue);
		return res;
	}
}
