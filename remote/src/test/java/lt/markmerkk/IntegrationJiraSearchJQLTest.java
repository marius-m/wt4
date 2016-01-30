package lt.markmerkk;

import java.io.FileInputStream;
import java.util.Properties;
import net.rcarz.jiraclient.JiraClient;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * Created by mariusmerkevicius on 1/29/16.
 */
public class IntegrationJiraSearchJQLTest {

  private Properties properties;
  private JiraClient client;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    FileInputStream in = null;
    in = new FileInputStream("integration_test.properties");
    properties = new Properties();
    properties.load(in);
//    ICredentials creds = new BasicCredentials((String) props.get("username"), (String) props.get("password"));
//    jira = new JiraClient((String) props.get("host"), creds);
  }

  @Test
  public void test_inputClient_shouldProceed() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();

    Observable<JiraClient> clientObservable = Observable.create(new JiraConnector(
        (String) properties.get("host"),
        (String) properties.get("username"),
        (String) properties.get("password")
    ));

    // Act
    clientObservable.subscribe(subscriber);

    // Assert
    subscriber.assertNoErrors(); // Should create successfully
  }

  @Test
  public void test_inputValid_shouldPullWorklogs() throws Exception {
    // Arrange

    // Forming jira client
    Observable.create(new JiraConnector(
        (String) properties.get("host"),
        (String) properties.get("username"),
        (String) properties.get("password")
    )).subscribe(jiraClient -> client = jiraClient);

    DateTime start = JiraSearchJQL.dateFormat.parseDateTime("2016-01-14");
    DateTime end = JiraSearchJQL.dateFormat.parseDateTime("2016-01-15");

    JiraLogFilterer filterer = new JiraLogFilterer(
        (String) properties.get("username"),
        start,
        end
    );

    // Act
    JiraObservables.remoteWorklogs(client, start, end, filterer)
        .subscribe(pair -> System.out.println(pair.getKey() + " / " + pair.getValue().size()));

    // Assert

  }

}