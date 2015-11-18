package com.atlassian.jira.rest.client.api.domain;

import com.google.common.base.Objects;

/**
 * Represents audit search metadata and audit result records
 */
public class AuditRecordsData {

    private final Integer offset;
    private final Integer limit;
    private final Integer total;
    private final Iterable<AuditRecord> records;

    public AuditRecordsData(final Integer offset, final Integer limit, final Integer total, final Iterable<AuditRecord> records) {
        this.offset = offset;
        this.limit = limit;
        this.total = total;
        this.records = records;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getTotal() {
        return total;
    }

    public Iterable<AuditRecord> getRecords() {
        return records;
    }

    protected Objects.ToStringHelper getToStringHelper() {
        return Objects.toStringHelper(this).
                add("offset", offset).
                add("limit", limit).
                add("total", total).
                add("records", records);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AuditRecordsData) {
            final AuditRecordsData that = (AuditRecordsData) o;
            return Objects.equal(this.offset, that.offset)
                    && Objects.equal(this.limit, that.limit)
                    && Objects.equal(this.total, that.total)
                    && Objects.equal(this.records, that.records);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(offset, limit, total, records);
    }
}
