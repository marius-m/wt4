package lt.markmerkk;

import java.io.FileInputStream;
import java.util.Properties;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

/**
 * Created by mariusmerkevicius on 1/29/16.
 */
public class IntegrationJiraSearchJQLTest {

  private Properties properties;
  private JiraConnector connector;
  private boolean loading;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    FileInputStream in = null;
    in = new FileInputStream("integration_test.properties");
    properties = new Properties();
    properties.load(in);

    connector = new JiraConnector(
        (String) properties.get("host"),
        (String) properties.get("username"),
        (String) properties.get("password")
    );

//    ICredentials creds = new BasicCredentials((String) props.get("username"), (String) props.get("password"));
//    jira = new JiraClient((String) props.get("host"), creds);
  }

  @Test
  public void test_inputClient_shouldProceed() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();

    // Act
    ConnectableObservable<Issue> issueObservable = Observable.create(connector)
        .flatMap(jiraClient -> JiraObservables.userIssues(jiraClient, JiraSearchJQL.DEFAULT_JQL_USER_ISSUES))
        .publish();
    issueObservable.subscribe(issue -> {
      System.out.println("Issue: "+issue);
    }, error -> {
      System.out.println("Error: "+error);
    }, () -> {
      System.out.println("Complete!");
    });

    loading = true;
    issueObservable.subscribe(issue -> {
    }, error -> {
      System.out.println("...error");
    }, () -> {
      System.out.println("...complete!");
    });

    issueObservable.connect();

    // Assert
    subscriber.assertNoErrors(); // Should create successfully
  }

}