package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @since v2.0
 */
public class AuditChangedValueJsonParser implements JsonObjectParser<AuditChangedValue> {

    @Override
    public AuditChangedValue parse(final JSONObject json) throws JSONException {
        final String fieldName = json.getString("fieldName");
        final String changedFrom = JsonParseUtil.getOptionalString(json, "changedFrom");
        final String changedTo = JsonParseUtil.getOptionalString(json, "changedTo");

        return new AuditChangedValue(fieldName, changedTo, changedFrom);
    }
}
