package lt.markmerkk.jira;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 1/9/16.
 */
public class WorkExecutorIsSyncingTest {

  private WorkExecutor executor;

  @Before
  public void setUp() {
    executor = mock(WorkExecutor.class);
    doCallRealMethod().when(executor).isSyncing();
  }

  @Test
  public void isSyncing_hasMoreAndLoading_returnTrue() throws Exception {
    // Arrange
    doReturn(true).when(executor).hasMore();
    doReturn(true).when(executor).isLoading();

    // Act
    boolean syncing = executor.isSyncing();

    // Assert
    assertThat(syncing).isTrue();
  }

  @Test
  public void isSyncing_hasMoreAndNotLoading_returnTrue() throws Exception {
    // Arrange
    doReturn(true).when(executor).hasMore();
    doReturn(false).when(executor).isLoading();

    // Act
    boolean syncing = executor.isSyncing();

    // Assert
    assertThat(syncing).isTrue();
  }

  @Test
  public void isSyncing_noMoreAndLoading_returnTrue() throws Exception {
    // Arrange
    doReturn(false).when(executor).hasMore();
    doReturn(true).when(executor).isLoading();

    // Act
    boolean syncing = executor.isSyncing();

    // Assert
    assertThat(syncing).isTrue();
  }

  @Test
  public void isSyncing_noMoreAndNotLoading_returnFalse() throws Exception {
    // Arrange
    doReturn(false).when(executor).hasMore();
    doReturn(false).when(executor).isLoading();

    // Act
    boolean syncing = executor.isSyncing();

    // Assert
    assertThat(syncing).isFalse();
  }
}