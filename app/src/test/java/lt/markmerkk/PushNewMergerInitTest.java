package lt.markmerkk;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/29/15.
 */
public class PushNewMergerInitTest {

  @Test public void testValid() throws Exception {
    // Arrange
    PushNewMerger
        merger = new PushNewMerger(mock(IExecutor.class), mock(JiraRestClient.class), DateTime.now());

    // Act
    // Assert
    assertThat(merger).isNotNull();
  }

  @Test public void testNullExecutor() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      PushNewMerger merger = new PushNewMerger(null, mock(JiraRestClient.class), DateTime.now());
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot function without database!");
    }
  }

  @Test public void testNullClient() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      PushNewMerger merger = new PushNewMerger(mock(IExecutor.class), null, DateTime.now());
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot function without a Jira client!");
    }
  }

  @Test public void testNullTargetDate() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      PushNewMerger
          merger = new PushNewMerger(mock(IExecutor.class), mock(JiraRestClient.class), null);
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot function without a target date!");
    }
  }
}