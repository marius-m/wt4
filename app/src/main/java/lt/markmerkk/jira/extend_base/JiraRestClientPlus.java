package lt.markmerkk.jira.extend_base;

import com.atlassian.jira.rest.client.api.JiraRestClient;

/**
 * Created by Frederic Meheus
 * https://answers.atlassian.com/questions/205807/rest-api-is-returning-only-20-worklogs-per-issue
 */
public interface JiraRestClientPlus extends JiraRestClient
{
    IssueWorklogsRestClient getIssueWorklogRestClient();
}