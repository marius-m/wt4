package lt.markmerkk.utils;

import org.junit.Test;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 2/2/16.
 */
public class ClientObservablesSearcInputTest {

  @Test
  public void test_inputInputValid_shouldModify() throws Exception {
    // Arrange
    TestSubscriber<String> subscriber = new TestSubscriber<>();
    String searchPhrase = "dokumentacija";

    // Act
    ClientObservables.issueSearchInputObservable(searchPhrase)
        .subscribe(subscriber);
    String output = subscriber.getOnNextEvents().get(0);

    // Assert
    subscriber.assertNoErrors();
    assertThat(output).isEqualTo("summary ~ \"dokumentacija\"");
  }

  @Test
  public void test_inputInputValidWithTask_shouldModify() throws Exception {
    // Arrange
    TestSubscriber<String> subscriber = new TestSubscriber<>();
    String searchPhrase = "tt15";

    // Act
    ClientObservables.issueSearchInputObservable(searchPhrase)
        .subscribe(subscriber);
    String output = subscriber.getOnNextEvents().get(0);

    // Assert
    subscriber.assertNoErrors();
    assertThat(output).isEqualTo("summary ~ \"tt15\" OR key = \"TT-15\"");
  }

  @Test
  public void test_inputNull_shouldReturnEmpty() throws Exception {
    // Arrange
    TestSubscriber<String> subscriber = new TestSubscriber<>();
    String searchPhrase = null;

    // Act
    ClientObservables.issueSearchInputObservable(searchPhrase)
        .subscribe(subscriber);

    // Assert
    subscriber.assertNoErrors();
    assertThat(subscriber.getOnNextEvents().size()).isZero();
  }
}