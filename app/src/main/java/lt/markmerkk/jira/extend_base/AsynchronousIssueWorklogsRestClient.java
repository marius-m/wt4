package lt.markmerkk.jira.extend_base;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.util.concurrent.Promise;
import java.net.URI;
import java.util.List;
import javax.ws.rs.core.UriBuilder;

/**
 * Created by Frederic Meheus
 * https://answers.atlassian.com/questions/205807/rest-api-is-returning-only-20-worklogs-per-issue
 */
public class AsynchronousIssueWorklogsRestClient extends AbstractAsynchronousRestClient implements IssueWorklogsRestClient {
    private final WorklogsJsonParser worklogsParser = new WorklogsJsonParser();
    private final URI baseUri;

    public AsynchronousIssueWorklogsRestClient(final URI baseUri, final HttpClient client)
    {
        super(client);
        this.baseUri = baseUri;
    }

    @Override
    public Promise<List<Worklog>> getIssueWorklogs(final BasicIssue issue)
    {
        final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
        uriBuilder.path("issue").path(issue.getKey()).path("worklog");
        worklogsParser.setIssue(issue);
        return getAndParse(uriBuilder.build(), worklogsParser);
    }
}