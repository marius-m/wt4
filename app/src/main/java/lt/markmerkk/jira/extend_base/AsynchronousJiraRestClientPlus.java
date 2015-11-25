package lt.markmerkk.jira.extend_base;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

/**
 * Created by Frederic Meheus
 * https://answers.atlassian.com/questions/205807/rest-api-is-returning-only-20-worklogs-per-issue
 */
public class AsynchronousJiraRestClientPlus extends AsynchronousJiraRestClient implements JiraRestClientPlus
{
    private final IssueWorklogsRestClient issueWorklogsRestClient;

    public AsynchronousJiraRestClientPlus(final URI serverUri, final DisposableHttpClient httpClient)
    {
        super(serverUri,httpClient);
        final URI baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build(); // '/rest/api/latest' or '/rest/api/2'
        issueWorklogsRestClient = new AsynchronousIssueWorklogsRestClient(baseUri, httpClient);
    }

    @Override
    public IssueWorklogsRestClient getIssueWorklogRestClient()
    {
        return issueWorklogsRestClient;
    }
}