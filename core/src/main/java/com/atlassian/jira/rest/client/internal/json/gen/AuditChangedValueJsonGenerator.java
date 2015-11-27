package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @since v2.0
 */
public class AuditChangedValueJsonGenerator implements JsonGenerator<AuditChangedValue> {
    @Override
    public JSONObject generate(AuditChangedValue bean) throws JSONException {
        final JSONObject obj = new JSONObject().put("fieldName", bean.getFieldName());
        if (bean.getChangedTo() != null) {
            obj.put("changedTo", bean.getChangedTo());
        }
        if (bean.getChangedFrom() != null) {
            obj.put("changedFrom", bean.getChangedFrom());
        }
        return obj;
    }
}
