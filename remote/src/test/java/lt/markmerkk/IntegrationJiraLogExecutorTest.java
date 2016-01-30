package lt.markmerkk;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import lt.markmerkk.interfaces.IRemoteListener;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.ICredentials;
import net.rcarz.jiraclient.JiraClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 1/28/16.
 */
@Ignore
public class IntegrationJiraLogExecutorTest {


  @Mock
  IRemoteListener remoteListener;
  @Mock
  IRemoteLoadListener remoteLoadListener;
  private JiraClient jira;
  private Properties properties;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    FileInputStream in = null;
    in = new FileInputStream("integration_test.properties");
    properties = new Properties();
    properties.load(in);
    ICredentials creds = new BasicCredentials((String) properties.get("username"), (String) properties.get("password"));
    jira = new JiraClient((String) properties.get("host"), creds);
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
  public void test_input_should() throws Exception {
    // Arrange
    JiraLogExecutor executor = spy(new JiraLogExecutor(remoteListener, remoteLoadListener));
    doReturn(true).when(executor).isLoading();

    // Act
    executor.runner(
        (String) properties.get("host"),
        (String) properties.get("username"),
        (String) properties.get("password"),
        JiraLogExecutor.dateFormat.parseDateTime("2016-01-14"),
        JiraLogExecutor.dateFormat.parseDateTime("2016-01-15")
    );

    // Assert
    verify(remoteListener).onWorklogDownloadComplete(any(Map.class));
  }

  @Test
  public void test_rx_should() throws Exception {
    // Arrange
    JiraLogExecutor executor = spy(new JiraLogExecutor(remoteListener, remoteLoadListener));
    doReturn(true).when(executor).isLoading();

    // Act
    executor.runner(
        (String) properties.get("host"),
        (String) properties.get("username"),
        (String) properties.get("password"),
        JiraLogExecutor.dateFormat.parseDateTime("2016-01-14"),
        JiraLogExecutor.dateFormat.parseDateTime("2016-01-15")
    );

    // Assert
    verify(remoteListener).onWorklogDownloadComplete(any(Map.class));
  }
}