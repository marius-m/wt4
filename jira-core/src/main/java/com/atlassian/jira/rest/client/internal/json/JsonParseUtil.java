/*
 * Copyright (C) 2010 Atlassian
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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.ExpandableProperty;
import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class JsonParseUtil {
	public static final String JIRA_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final DateTimeFormatter JIRA_DATE_TIME_FORMATTER = DateTimeFormat.forPattern(JIRA_DATE_TIME_PATTERN);
	public static final DateTimeFormatter JIRA_DATE_FORMATTER = ISODateTimeFormat.date();
	public static final String SELF_ATTR = "self";

	public static <T> Collection<T> parseJsonArray(final JSONArray jsonArray, final JsonObjectParser<T> jsonParser)
			throws JSONException {
		final Collection<T> res = new ArrayList<T>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			res.add(jsonParser.parse(jsonArray.getJSONObject(i)));
		}
		return res;
	}

	public static <T> OptionalIterable<T> parseOptionalJsonArray(final JSONArray jsonArray, final JsonObjectParser<T> jsonParser)
			throws JSONException {
		if (jsonArray == null) {
			return OptionalIterable.absent();
		} else {
			return new OptionalIterable<T>(JsonParseUtil.<T>parseJsonArray(jsonArray, jsonParser));
		}
	}

	public static <T> T parseOptionalJsonObject(final JSONObject json, final String attributeName, final JsonObjectParser<T> jsonParser)
			throws JSONException {
		JSONObject attributeObject = getOptionalJsonObject(json, attributeName);
		return attributeObject != null ? jsonParser.parse(attributeObject) : null;
	}

	@SuppressWarnings("UnusedDeclaration")
	public static <T> ExpandableProperty<T> parseExpandableProperty(final JSONObject json, final JsonObjectParser<T> expandablePropertyBuilder)
			throws JSONException {
		return parseExpandableProperty(json, false, expandablePropertyBuilder);
	}

	@Nullable
	public static <T> ExpandableProperty<T> parseOptionalExpandableProperty(@Nullable final JSONObject json, final JsonObjectParser<T> expandablePropertyBuilder)
			throws JSONException {
		return parseExpandableProperty(json, true, expandablePropertyBuilder);
	}

	@Nullable
	private static <T> ExpandableProperty<T> parseExpandableProperty(@Nullable final JSONObject json, final Boolean optional, final JsonObjectParser<T> expandablePropertyBuilder)
			throws JSONException {
		if (json == null) {
			if (!optional) {
				throw new IllegalArgumentException("json object cannot be null while optional is false");
			}
			return null;
		}

		final int numItems = json.getInt("size");
		final Collection<T> items;
		JSONArray itemsJa = json.getJSONArray("items");

		if (itemsJa.length() > 0) {
			items = new ArrayList<T>(numItems);
			for (int i = 0; i < itemsJa.length(); i++) {
				final T item = expandablePropertyBuilder.parse(itemsJa.getJSONObject(i));
				items.add(item);
			}
		} else {
			items = null;
		}

		return new ExpandableProperty<T>(numItems, items);
	}


	public static URI getSelfUri(final JSONObject jsonObject) throws JSONException {
		return parseURI(jsonObject.getString(SELF_ATTR));
	}

	public static URI optSelfUri(final JSONObject jsonObject, final URI defaultUri) throws JSONException {
		final String selfUri = jsonObject.optString(SELF_ATTR, null);
		return selfUri != null ? parseURI(selfUri) : defaultUri;
	}

	@SuppressWarnings("unused")
	public static JSONObject getNestedObject(JSONObject json, final String... path) throws JSONException {
		for (String s : path) {
			json = json.getJSONObject(s);
		}
		return json;
	}

	@Nullable
	public static JSONObject getNestedOptionalObject(JSONObject json, final String... path) throws JSONException {
		for (int i = 0; i < path.length - 1; i++) {
			String s = path[i];
			json = json.getJSONObject(s);
		}
		return json.optJSONObject(path[path.length - 1]);
	}

	@SuppressWarnings("unused")
	public static JSONArray getNestedArray(JSONObject json, final String... path) throws JSONException {
		for (int i = 0; i < path.length - 1; i++) {
			String s = path[i];
			json = json.getJSONObject(s);
		}
		return json.getJSONArray(path[path.length - 1]);
	}

	public static JSONArray getNestedOptionalArray(JSONObject json, final String... path) throws JSONException {
		for (int i = 0; json != null && i < path.length - 1; i++) {
			String s = path[i];
			json = json.optJSONObject(s);
		}
		return json == null ? null : json.optJSONArray(path[path.length - 1]);
	}


	public static String getNestedString(JSONObject json, final String... path) throws JSONException {
		for (int i = 0; i < path.length - 1; i++) {
			String s = path[i];
			json = json.getJSONObject(s);
		}
		return json.getString(path[path.length - 1]);
	}

	@SuppressWarnings("unused")
	public static boolean getNestedBoolean(JSONObject json, final String... path) throws JSONException {
		for (int i = 0; i < path.length - 1; i++) {
			String s = path[i];
			json = json.getJSONObject(s);
		}
		return json.getBoolean(path[path.length - 1]);
	}


	public static URI parseURI(final String str) {
		try {
			return new URI(str);
		} catch (URISyntaxException e) {
			throw new RestClientException(e);
		}
	}

	@Nullable
	public static URI parseOptionalURI(final JSONObject jsonObject, final String attributeName) {
		final String s = getOptionalString(jsonObject, attributeName);
		return s != null ? parseURI(s) : null;
	}

	@Nullable
	public static BasicUser parseBasicUser(@Nullable final JSONObject json) throws JSONException {
		if (json == null) {
			return null;
		}
		final String username = json.getString("name");
		if (!json.has(JsonParseUtil.SELF_ATTR) && "Anonymous".equals(username)) {
			return null; // insane representation for unassigned user - JRADEV-4262
		}

		// deleted user? BUG in REST API: JRA-30263
		final URI selfUri = optSelfUri(json, BasicUser.INCOMPLETE_URI);
		return new BasicUser(selfUri, username, json.optString("displayName", null));
	}

	public static DateTime parseDateTime(final JSONObject jsonObject, final String attributeName) throws JSONException {
		return parseDateTime(jsonObject.getString(attributeName));
	}

	@Nullable
	public static DateTime parseOptionalDateTime(final JSONObject jsonObject, final String attributeName) throws JSONException {
		final String s = getOptionalString(jsonObject, attributeName);
		return s != null ? parseDateTime(s) : null;
	}

	public static DateTime parseDateTime(final String str) {
		try {
			return JIRA_DATE_TIME_FORMATTER.parseDateTime(str);
		} catch (Exception e) {
			throw new RestClientException(e);
		}
	}

	/**
	 * Tries to parse date and time and return that. If fails then tries to parse date only.
	 *
	 * @param str String contains either date and time or date only
	 * @return date and time or date only
	 */
	public static DateTime parseDateTimeOrDate(final String str) {
		try {
			return JIRA_DATE_TIME_FORMATTER.parseDateTime(str);
		} catch (Exception ignored) {
			try {
				return JIRA_DATE_FORMATTER.parseDateTime(str);
			} catch (Exception e) {
				throw new RestClientException(e);
			}
		}
	}

	public static DateTime parseDate(final String str) {
		try {
			return JIRA_DATE_FORMATTER.parseDateTime(str);
		} catch (Exception e) {
			throw new RestClientException(e);
		}
	}

	public static String formatDate(final DateTime dateTime) {
		return JIRA_DATE_FORMATTER.print(dateTime);
	}

	@SuppressWarnings("unused")
	public static String formatDateTime(final DateTime dateTime) {
		return JIRA_DATE_TIME_FORMATTER.print(dateTime);
	}


	@Nullable
	public static String getNullableString(final JSONObject jsonObject, final String attributeName) throws JSONException {
		final Object o = jsonObject.get(attributeName);
		if (o == JSONObject.NULL) {
			return null;
		}
		return o.toString();
	}


	@Nullable
	public static String getOptionalString(final JSONObject jsonObject, final String attributeName) {
		final Object res = jsonObject.opt(attributeName);
		if (res == JSONObject.NULL || res == null) {
			return null;
		}
		return res.toString();
	}

	@Nullable
	public static <T> T getOptionalJsonObject(final JSONObject jsonObject, final String attributeName, final JsonObjectParser<T> jsonParser) throws JSONException {
		final JSONObject res = jsonObject.optJSONObject(attributeName);
		if (res == JSONObject.NULL || res == null) {
			return null;
		}
		return jsonParser.parse(res);
	}

    @SuppressWarnings("unused")
	@Nullable
	public static JSONObject getOptionalJsonObject(final JSONObject jsonObject, final String attributeName) {
		final JSONObject res = jsonObject.optJSONObject(attributeName);
		if (res == JSONObject.NULL || res == null) {
			return null;
		}
		return res;
	}


	public static Collection<String> toStringCollection(final JSONArray jsonArray) throws JSONException {
		final ArrayList<String> res = new ArrayList<String>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			res.add(jsonArray.getString(i));
		}
		return res;
	}

	public static Integer parseOptionInteger(final JSONObject json, final String attributeName) throws JSONException {
		return json.has(attributeName) ? json.getInt(attributeName) : null;
	}

	@Nullable
	public static Long getOptionalLong(final JSONObject jsonObject, final String attributeName) throws JSONException {
		return jsonObject.has(attributeName) ? jsonObject.getLong(attributeName) : null;
	}

	public static Optional<JSONArray> getOptionalArray(final JSONObject jsonObject, final String attributeName)
			throws JSONException {
		return jsonObject.has(attributeName) ?
				Optional.of(jsonObject.getJSONArray(attributeName)) : Optional.<JSONArray>absent();
	}

	public static Map<String, URI> getAvatarUris(final JSONObject jsonObject) throws JSONException {
		Map<String, URI> uris = Maps.newHashMap();

		final Iterator iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			final Object o = iterator.next();
			if (!(o instanceof String)) {
				throw new JSONException(
						"Cannot parse URIs: key is expected to be valid String. Got " + (o == null ? "null" : o.getClass())
								+ " instead.");
			}
			final String key = (String) o;
			uris.put(key, JsonParseUtil.parseURI(jsonObject.getString(key)));
		}
		return uris;
	}

	@SuppressWarnings("unchecked")
	public static Iterator<String> getStringKeys(final JSONObject json) {
		return json.keys();
	}

	public static Map<String, String> toStringMap(final JSONArray names, final JSONObject values) throws JSONException {
		final Map<String, String> result = Maps.newHashMap();
		for (int i = 0; i < names.length(); i++) {
			final String key = names.getString(i);
			result.put(key, values.getString(key));
		}
		return result;
	}
}