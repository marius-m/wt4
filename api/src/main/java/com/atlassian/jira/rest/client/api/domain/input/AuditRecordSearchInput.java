package com.atlassian.jira.rest.client.api.domain.input;

import org.joda.time.DateTime;

import javax.annotation.Nullable;

/**
 * Input data for searching audit records
 *
 * @since v2.0.0
 */
public class AuditRecordSearchInput {

    @Nullable
    private final Integer offset;
    @Nullable
    private final Integer limit;
    @Nullable
    private final String textFilter;
    @Nullable
    private final DateTime from;
    @Nullable
    private final DateTime to;

    public AuditRecordSearchInput(final Integer offset, final Integer limit, final String textFilter, final DateTime from, final DateTime to) {
        this.offset = offset;
        this.limit = limit;
        this.textFilter = textFilter;
        this.from = from;
        this.to = to;
    }

    @Nullable
    public Integer getOffset() {
        return offset;
    }

    @Nullable
    public Integer getLimit() {
        return limit;
    }

    @Nullable
    public String getTextFilter() {
        return textFilter;
    }

    @Nullable
    public DateTime getFrom() {
        return from;
    }

    @Nullable
    public DateTime getTo() {
        return to;
    }
}
