package com.atlassian.jira.rest.client.api.domain;

import com.atlassian.jira.rest.client.api.OptionalIterable;

import com.google.common.base.Objects;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

/**
 * Represents record from JIRA Audit Log.
 *
 * @since v2.0
 */
public class AuditRecord {

    private final Long id;

    private final String summary;

    private final DateTime created;

    private final String category;

    private final String eventSource;

    @Nullable
    private final String authorKey;

    @Nullable
    private final String remoteAddress;

    @Nullable
    private final AuditAssociatedItem objectItem;

    private final OptionalIterable<AuditAssociatedItem> associatedItem;

    private final OptionalIterable<AuditChangedValue> changedValues;

    public AuditRecord(final Long id, final String summary, @Nullable final String remoteAddress,
                       final DateTime created, final String category, String eventSource,
                       @Nullable final String authorKey,
                       @Nullable final AuditAssociatedItem objectItem,
                       final OptionalIterable<AuditAssociatedItem> associatedItem,
                       final OptionalIterable<AuditChangedValue> changedValues) {
        this.id = id;
        this.summary = summary;
        this.remoteAddress = remoteAddress;
        this.created = created;
        this.category = category;
        this.eventSource = eventSource;
        this.authorKey = authorKey;
        this.objectItem = objectItem;
        this.associatedItem = associatedItem;
        this.changedValues = changedValues;
    }

    public Long getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public DateTime getCreated() {
        return created;
    }

    public String getCategory() {
        return category;
    }

    public String getEventSource() {
        return eventSource;
    }

    @Nullable
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Nullable
    public String getAuthorKey() {
        return authorKey;
    }

    @Nullable
    public AuditAssociatedItem getObjectItem() {
        return objectItem;
    }

    public OptionalIterable<AuditAssociatedItem> getAssociatedItems() {
        return associatedItem;
    }

    public OptionalIterable<AuditChangedValue> getChangedValues() {
        return changedValues;
    }

    protected Objects.ToStringHelper getToStringHelper() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("summary", summary).
                add("remoteAddress", remoteAddress).
                add("created", created).
                add("category", category).
                add("authorKey", authorKey).
                add("objectItem", objectItem).
                add("associatedItem", associatedItem).
                add("changedValues", changedValues);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AuditRecord) {
            final AuditRecord that = (AuditRecord) o;
            return  Objects.equal(this.id, that.id)
                    && Objects.equal(this.summary, that.summary)
                    && Objects.equal(this.remoteAddress, that.remoteAddress)
                    && Objects.equal(this.created, that.created)
                    && Objects.equal(this.category, that.category)
                    && Objects.equal(this.authorKey, that.authorKey)
                    && Objects.equal(this.objectItem, that.objectItem)
                    && Objects.equal(this.associatedItem, that.associatedItem)
                    && Objects.equal(this.changedValues, that.changedValues);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, summary, remoteAddress, created, category, authorKey, objectItem, associatedItem, changedValues);
    }

}
