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

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TimeTrackingJsonParser implements JsonObjectParser<TimeTracking> {
	@Override
	public TimeTracking parse(JSONObject json) throws JSONException {
		final Integer originalEstimateMinutes = JsonParseUtil.parseOptionInteger(json, "timeoriginalestimate");
		final Integer timeRemainingMinutes = JsonParseUtil.parseOptionInteger(json, "timeestimate");
		final Integer timeSpentMinutes = JsonParseUtil.parseOptionInteger(json, "timespent");
		return new TimeTracking(originalEstimateMinutes, timeRemainingMinutes, timeSpentMinutes);
	}

}
