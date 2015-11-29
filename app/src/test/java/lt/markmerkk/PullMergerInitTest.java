package lt.markmerkk;

import com.atlassian.jira.rest.client.api.domain.Worklog;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/29/15.
 */
public class PullMergerInitTest {
  @Test public void testValid() throws Exception {
    // Arrange
    PullMerger merger = new PullMerger(mock(IExecutor.class), "asdf", mock(Worklog.class));

    // Act
    // Assert
    assertThat(merger).isNotNull();
  }

  @Test public void testNullDb() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new PullMerger(null, "asdf", mock(Worklog.class));
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot function without database!");
    }
  }

  @Test public void testNullIssue() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new PullMerger(mock(IExecutor.class), null, mock(Worklog.class));
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot function without issue name!");
    }
  }

  @Test public void testNullWorklog() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new PullMerger(mock(IExecutor.class), "TT-123", null);
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot function without remote worklog!");
    }
  }
}