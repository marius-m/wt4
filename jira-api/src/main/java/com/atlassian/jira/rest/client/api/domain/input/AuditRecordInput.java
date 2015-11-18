package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.util.concurrent.Nullable;
import com.google.common.base.Objects;

/**
 * Represents record from JIRA Audit Log.
 *
 * @since v2.0
 */
public class AuditRecordInput {

    private final String summary;

    private final String category;

    @Nullable
    private final AuditAssociatedItem objectItem;

    @Nullable
    private final Iterable<AuditAssociatedItem> associatedItem;

    @Nullable
    private final Iterable<AuditChangedValue> changedValues;

    public AuditRecordInput(final String category, final String summary,
                       @Nullable final AuditAssociatedItem objectItem,
                       @Nullable final Iterable<AuditAssociatedItem> associatedItem,
                       @Nullable final Iterable<AuditChangedValue> changedValues) {
        this.summary = summary;
        this.category = category;
        this.objectItem = objectItem;
        this.associatedItem = associatedItem;
        this.changedValues = changedValues;
    }

    public String getSummary() {
        return summary;
    }

    public String getCategory() {
        return category;
    }

    public AuditAssociatedItem getObjectItem() {
        return objectItem;
    }

    public Iterable<AuditAssociatedItem> getAssociatedItems() {
        return associatedItem;
    }

    public Iterable<AuditChangedValue> getChangedValues() {
        return changedValues;
    }

    protected Objects.ToStringHelper getToStringHelper() {
        return Objects.toStringHelper(this).
                add("summary", summary).
                add("category", category).
                add("objectItem", objectItem).
                add("associatedItem", associatedItem).
                add("changedValues", changedValues);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AuditRecordInput) {
            final AuditRecordInput that = (AuditRecordInput) o;
            return Objects.equal(this.summary, that.summary)
                    && Objects.equal(this.category, that.category)
                    && Objects.equal(this.objectItem, that.objectItem)
                    && Objects.equal(this.associatedItem, that.associatedItem)
                    && Objects.equal(this.changedValues, that.changedValues);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(summary, category, objectItem, associatedItem, changedValues);
    }

}
