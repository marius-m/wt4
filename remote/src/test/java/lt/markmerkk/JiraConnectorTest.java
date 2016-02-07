package lt.markmerkk;

import net.rcarz.jiraclient.JiraClient;
import org.junit.Test;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 1/29/16.
 */
public class JiraConnectorTest {
  @Test
  public void init_inputValid_shouldCreate() throws Exception {
    // Arrange
    Observable<JiraClient> jiraObservable =
        Observable.create(new JiraConnector("Somehostname", "marius.m@marius.lt", "somepass"));

    // Act
    // Assert
    assertThat(jiraObservable);
  }

  @Test
  public void init_inputHostnameNull_shouldCreate() throws Exception {
    // Arrange
    Observable<JiraClient> jiraObservable =
        Observable.create(new JiraConnector(null, "marius.m@marius.lt", "somepass"));

    // Act
    // Assert
    assertThat(jiraObservable);
  }

  @Test
  public void init_inputUsernameNull_shouldCreate() throws Exception {
    // Arrange
    Observable<JiraClient> jiraObservable =
        Observable.create(new JiraConnector("Somehostname", null, "somepass"));

    // Act
    // Assert
    assertThat(jiraObservable);
  }

  @Test
  public void init_inputPasswordNull_shouldCreate() throws Exception {
    // Arrange
    Observable<JiraClient> jiraObservable =
        Observable.create(new JiraConnector("Somehostname", "marius.m@marius.lt", null));

    // Act
    // Assert
    assertThat(jiraObservable);
  }

}