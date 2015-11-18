package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @since v2.0
 */
public class AuditAssociatedItemJsonParser implements JsonObjectParser<AuditAssociatedItem> {

    @Override
    public AuditAssociatedItem parse(final JSONObject json) throws JSONException {

        final String id = JsonParseUtil.getOptionalString(json, "id");
        final String name = json.getString("name");
        final String typeName = json.getString("typeName");
        final String parentId = JsonParseUtil.getOptionalString(json, "parentId");
        final String parentName = JsonParseUtil.getOptionalString(json, "parentName");

        return new AuditAssociatedItem(id, name, typeName, parentId, parentName);
    }
}
