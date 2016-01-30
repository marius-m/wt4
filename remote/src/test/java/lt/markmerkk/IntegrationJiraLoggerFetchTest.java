package lt.markmerkk;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import lt.markmerkk.interfaces.IRemoteListener;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.ICredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 1/27/16.
 */
@Ignore
public class IntegrationJiraLoggerFetchTest {

  @Mock IRemoteListener remoteListener;
  @Mock IRemoteLoadListener remoteLoadListener;
  private JiraClient jira;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    FileInputStream in = null;
    in = new FileInputStream("integration_test.properties");
    Properties props = new Properties();
    props.load(in);
    ICredentials creds = new BasicCredentials((String) props.get("username"), (String) props.get("password"));
    jira = new JiraClient((String) props.get("host"), creds);
  }

  @Test
  public void test_validClient_shouldNotBeNull() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(jira).isNotNull();
    System.out.println(jira);
  }

  @Test
  public void test_fullTest_shouldExecute() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(jira).isNotNull();
    System.out.println(jira);
  }

  @Test
  public void test_inputSearchResult_shouldOutputReal() throws Exception {
    // Arrange
    JiraLogger logger = new JiraLogger(remoteListener, remoteLoadListener);
    final String jql = String.format(JiraLogger.JQL_WORKLOG_TEMPLATE,
        "2016-01-14",
        "2016-01-15",
        jira.getSelf()
    );

    // Act
    TestSubscriber<Issue.SearchResult> testSubscriber = new TestSubscriber<>();
    logger.searchResult(jira, jql).subscribe(testSubscriber);
    List<Issue.SearchResult> onNextEvents = testSubscriber.getOnNextEvents();

    // Assert
    testSubscriber.assertNoErrors();
    assertThat(onNextEvents).isNotNull();
    assertThat(onNextEvents.size()).isEqualTo(1);

    Observable.just(onNextEvents.get(0))
        .map(new Func1<Issue.SearchResult, List<Issue>>() {
          @Override
          public List<Issue> call(Issue.SearchResult searchResult) {
            return searchResult.issues;
          }
        })
        .flatMap(new Func1<List<Issue>, Observable<Issue>>() {
          @Override
          public Observable<Issue> call(List<Issue> issues) {
            return Observable.from(issues);
          }
        })
        .subscribe(new Action1<Issue>() {
          @Override
          public void call(Issue issue) {
            System.out.println("Issue: "+issue);
          }
        });
  }
}