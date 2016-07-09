package lt.markmerkk;

import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 1/29/16.
 */
public class JiraConnectorCallTest {
  @Test
  public void init_inputValid_shouldCreate() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();
    Observable<JiraClient> observable = Observable.create(new JiraConnector("Somehostname", "marius.m@marius.lt", "somepass"));

    // Act
    observable.subscribe(subscriber);

    // Assert
    subscriber.assertCompleted();
    subscriber.assertNoErrors();
  }

  @Test
  public void init_inputHostnameNull_shouldCreate() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();
    Observable<JiraClient> observable = Observable.create(new JiraConnector(null, "marius.m@marius.lt", "somepass"));

    // Act
    observable.subscribe(subscriber);

    // Assert
    subscriber.assertError(IllegalStateException.class);
  }

  @Test
  public void init_inputHostnameEmpty_shouldCreate() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();
    Observable<JiraClient> observable = Observable.create(new JiraConnector("", "marius.m@marius.lt", "somepass"));

    // Act
    observable.subscribe(subscriber);

    // Assert
    subscriber.assertError(IllegalStateException.class);
  }

  @Test
  public void init_inputUsernameNull_shouldCreate() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();
    Observable<JiraClient> observable = Observable.create(new JiraConnector("hostname", null, "somepass"));

    // Act
    observable.subscribe(subscriber);

    // Assert
    subscriber.assertError(IllegalStateException.class);
  }

  @Test
  public void init_inputUsernameEmpty_shouldCreate() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();
    Observable<JiraClient> observable = Observable.create(new JiraConnector("hostname", "", "somepass"));

    // Act
    observable.subscribe(subscriber);

    // Assert
    subscriber.assertError(IllegalStateException.class);
  }

  @Test
  public void init_inputPasswordNull_shouldCreate() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();
    Observable<JiraClient> observable = Observable.create(new JiraConnector("hostname", "marius.m@marius.lt", null));

    // Act
    observable.subscribe(subscriber);

    // Assert
    subscriber.assertError(IllegalStateException.class);
  }

  @Test
  public void init_inputPasswordEmpty_shouldCreate() throws Exception {
    // Arrange
    TestSubscriber<JiraClient> subscriber = new TestSubscriber<>();
    Observable<JiraClient> observable = Observable.create(new JiraConnector("hostname", "marius.m@marius.lt", ""));

    // Act
    observable.subscribe(subscriber);

    // Assert
    subscriber.assertError(IllegalStateException.class);
  }

}