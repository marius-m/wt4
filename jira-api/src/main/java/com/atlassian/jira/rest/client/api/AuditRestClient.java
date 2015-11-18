package com.atlassian.jira.rest.client.api;

import com.atlassian.jira.rest.client.api.domain.AuditRecordInput;
import com.atlassian.jira.rest.client.api.domain.AuditRecordsData;
import com.atlassian.jira.rest.client.api.domain.input.AuditRecordSearchInput;
import com.atlassian.util.concurrent.Promise;

import javax.annotation.Nonnull;

/**
 * The com.atlassian.jira.rest.client.api handling audit record resources
 *
 * @since v2.0
 */
public interface AuditRestClient {

    Promise<AuditRecordsData> getAuditRecords(AuditRecordSearchInput input);

    void addAuditRecord(@Nonnull AuditRecordInput record);

}
