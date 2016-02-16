package lt.markmerkk.storage2;

import lt.markmerkk.storage2.database.interfaces.IExecutor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
public class RemoteFetchIssueInitTest {

  @Test
  public void test_inputValid_shouldCreate() throws Exception {
    // Arrange
    RemoteFetchIssue fetchIssue = new RemoteFetchIssue(mock(IExecutor.class), 1234);

    // Act
    // Assert
    assertThat(fetchIssue).isNotNull();
  }

  @Test
  public void init_inputNullExecutor_shouldThrow() throws Exception {
    // Arrange
    RemoteFetchIssue fetchIssue = null;
    try {
      fetchIssue = new RemoteFetchIssue(null, 12345);
      fail("Should not proceed");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("executor == null");
    }

    // Act
    // Assert
    assertThat(fetchIssue).isNull();
  }

  @Test
  public void init_inputNullDownloadMillis_shouldThrow() throws Exception {
    // Arrange
    RemoteFetchIssue fetchIssue = null;
    try {
      fetchIssue = new RemoteFetchIssue(mock(IExecutor.class), 0);
      fail("Should not proceed");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("downloadMillis == 0");
    }

    // Act
    // Assert
    assertThat(fetchIssue).isNull();
  }

}