package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @since v2.0
 */
public class AuditAssociatedItemJsonGenerator implements JsonGenerator<AuditAssociatedItem> {
    @Override
    public JSONObject generate(AuditAssociatedItem bean) throws JSONException {
        return new JSONObject()
                .put("id", bean.getId())
                .put("name", bean.getName())
                .put("typeName", bean.getTypeName())
                .put("parentId", bean.getParentId())
                .put("parentName", bean.getParentName());
    }
}
