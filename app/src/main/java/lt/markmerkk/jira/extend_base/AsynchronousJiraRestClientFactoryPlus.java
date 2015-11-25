package lt.markmerkk.jira.extend_base;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import java.net.URI;

/**
 * Created by Frederic Meheus
 * https://answers.atlassian.com/questions/205807/rest-api-is-returning-only-20-worklogs-per-issue
 */
public class AsynchronousJiraRestClientFactoryPlus extends AsynchronousJiraRestClientFactory
{
    @Override
    public JiraRestClientPlus create(final URI serverUri, final AuthenticationHandler authenticationHandler)
    {
        final DisposableHttpClient httpClient = new AsynchronousHttpClientFactory()
                .createClient(serverUri, authenticationHandler);
        return new AsynchronousJiraRestClientPlus(serverUri, httpClient);
    }

    @Override
    public JiraRestClientPlus createWithBasicHttpAuthentication(final URI serverUri, final String username, final String password)
    {
        return create(serverUri, new BasicHttpAuthenticationHandler(username, password));
    }

    @Override
    public JiraRestClientPlus create(final URI serverUri, final HttpClient httpClient)
    {
        final DisposableHttpClient disposableHttpClient = new AsynchronousHttpClientFactory().createClient(httpClient);
        return new AsynchronousJiraRestClientPlus(serverUri, disposableHttpClient);
    }
}