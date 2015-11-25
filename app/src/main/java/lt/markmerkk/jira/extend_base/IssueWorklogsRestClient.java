package lt.markmerkk.jira.extend_base;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.util.concurrent.Promise;
import java.util.List;

/**
 * Created by Frederic Meheus
 * https://answers.atlassian.com/questions/205807/rest-api-is-returning-only-20-worklogs-per-issue
 */
public interface IssueWorklogsRestClient {
    Promise<List<Worklog>> getIssueWorklogs(BasicIssue issue);
}