package lt.markmerkk;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.WorkLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

/**
 * Created by mariusmerkevicius on 1/29/16.
 */
public class IntegrationJiraLogger2Test {

  private Properties properties;

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
  public void test_inputLogger_shouldProceed() throws Exception {
    // Arrange
    TestSubscriber<Map<String, List<WorkLog>>> subscriber = new TestSubscriber<>();

    Observable<Map<String, List<WorkLog>>> loggerObservable = Observable.create(
        new JiraLogger2(
            null,
            JiraLogExecutor.dateFormat.parseDateTime("2016-01-14"),
            JiraLogExecutor.dateFormat.parseDateTime("2016-01-15")
        )
    );

    // Act
    loggerObservable.subscribe(subscriber);


    // Assert
    subscriber.assertError(JiraException.class); // No client provided
  }

  @Test
  public void test_combine_shouldConnect() throws Exception {
    // Arrange
    TestSubscriber<Map<String, List<WorkLog>>> subscriber = new TestSubscriber<>();

    Observable.create(new JiraConnector(
        (String) properties.get("host"),
        (String) properties.get("username"),
        (String) properties.get("password")
    )).flatMap(jiraClient -> Observable.create(new JiraLogger2(
        jiraClient,
        JiraLogExecutor.dateFormat.parseDateTime("2016-01-14"),
        JiraLogExecutor.dateFormat.parseDateTime("2016-01-15")
    ))).subscribe(subscriber);

    // Act
    // Assert
    subscriber.assertNoErrors(); // Should retrieve data!
  }

}