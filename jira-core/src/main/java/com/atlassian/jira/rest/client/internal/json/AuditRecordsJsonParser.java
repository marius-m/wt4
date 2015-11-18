package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import com.atlassian.jira.rest.client.api.domain.AuditRecord;
import com.atlassian.jira.rest.client.api.domain.AuditRecordsData;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

/**
 * @since v2.0
 */
public class AuditRecordsJsonParser implements JsonObjectParser<AuditRecordsData> {

    private final AuditAssociatedItemJsonParser associatedItemJsonParser = new AuditAssociatedItemJsonParser();
    private final AuditChangedValueJsonParser changedValueJsonParser = new AuditChangedValueJsonParser();
    private final SingleAuditRecordJsonParser singleAuditRecordJsonParser = new SingleAuditRecordJsonParser();

    @Override
    public AuditRecordsData parse(final JSONObject json) throws JSONException {
        final Integer offset = json.getInt("offset");
        final Integer limit = json.getInt("limit");
        final Integer total = json.getInt("total");
        final OptionalIterable<AuditRecord> records = JsonParseUtil.parseOptionalJsonArray(json.optJSONArray("records"), singleAuditRecordJsonParser);

        return new AuditRecordsData(offset, limit, total, records);
    }

    class SingleAuditRecordJsonParser implements  JsonObjectParser<AuditRecord> {
        @Override
        public AuditRecord parse(final JSONObject json) throws JSONException {
            final Long id =  json.getLong("id");
            final String summary = json.getString("summary");

            final String createdString = json.getString("created");
            final DateTime created = JsonParseUtil.parseDateTime(json, "created");
            final String category = json.getString("category");
            final String eventSource = json.getString("eventSource");
            final String authorKey = JsonParseUtil.getOptionalString(json, "authorKey");
            final String remoteAddress = JsonParseUtil.getOptionalString(json, "remoteAddress");
            final AuditAssociatedItem objectItem = JsonParseUtil.getOptionalJsonObject(json, "objectItem", associatedItemJsonParser);
            final OptionalIterable<AuditAssociatedItem> associatedItem = JsonParseUtil.parseOptionalJsonArray(json.optJSONArray("associatedItems"), associatedItemJsonParser);
            final OptionalIterable<AuditChangedValue> changedValues = JsonParseUtil.parseOptionalJsonArray(json.optJSONArray("changedValues"), changedValueJsonParser);

            return new AuditRecord(id, summary, remoteAddress, created, category, eventSource, authorKey, objectItem, associatedItem, changedValues);
        }

    }
}
