package lt.markmerkk;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 2/2/16.
 */
public class JiraObservablesIssueJQLTest {
  @Test
  public void test_inputValid_shouldFormJql() throws Exception {
    // Arrange
    DateTime startDate = new DateTime(10000);
    DateTime endDate = new DateTime(20000);
    String user = "marius.m";

    // Act
    String output = JiraObservables.issueSearchDateRangeObservable(startDate, endDate, user)
        .toBlocking().first();

    // Assert
    assertThat(output).isNotNull();
    assertThat(output).isEqualTo("key in workedIssues(\"1970-01-01\", \"1970-01-01\", \"marius.m\")");
  }

  @Test
  public void test_inputNoStart_shouldError() throws Exception {
    // Arrange
    DateTime startDate = null;
    DateTime endDate = new DateTime(20000);
    String user = "marius.m";

    // Act
    String output = null;
    try {
      output = JiraObservables.issueSearchDateRangeObservable(startDate, endDate, user)
          .toBlocking().first();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Start date invalid");
    }

    // Assert
    assertThat(output).isNull();
  }

  @Test
  public void test_inputNoEnd_shouldError() throws Exception {
    // Arrange
    DateTime startDate = new DateTime(10000);
    DateTime endDate = null;
    String user = "marius.m";

    // Act
    String output = null;
    try {
      output = JiraObservables.issueSearchDateRangeObservable(startDate, endDate, user)
          .toBlocking().first();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("End date invalid");
    }

    // Assert
    assertThat(output).isNull();
  }

  @Test
  public void test_inputNoUser_shouldError() throws Exception {
    // Arrange
    DateTime startDate = new DateTime(10000);
    DateTime endDate = new DateTime(20000);
    String user = null;

    // Act
    String output = null;
    try {
      output = JiraObservables.issueSearchDateRangeObservable(startDate, endDate, user)
          .toBlocking().first();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Invalid user");
    }

    // Assert
    assertThat(output).isNull();
  }
}