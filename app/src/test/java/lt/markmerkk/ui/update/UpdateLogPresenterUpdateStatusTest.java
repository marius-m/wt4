package lt.markmerkk.ui.update;

import lt.markmerkk.storage2.SimpleLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 4/9/16.
 */
public class UpdateLogPresenterUpdateStatusTest {

  public static final long VALID_SERVER_ID = 100L;
  public static final long NO_SERVER_ID = 0L;
  @Mock
  UpdateLogPresenter presenter;

  @Mock
  SimpleLog entity;


  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    doCallRealMethod().when(presenter).updateStatus(any(SimpleLog.class));
  }

  @Test
  public void updateStatus_inputNull_shouldDoNothing() throws Exception {
    // Arrange
    // Act
    presenter.updateStatus(null);

    // Assert
    verify(presenter, never()).setStatusInSync();
    verify(presenter, never()).setStatusUnSync();
    verify(presenter, never()).setStatusError(anyString());
  }

  @Test
  public void updateStatus_inputUnsynced_shouldSetUnsynced() throws Exception {
    // Arrange
    doReturn(false).when(entity).isError();
    doReturn(NO_SERVER_ID).when(entity).getId();

    // Act
    presenter.updateStatus(entity);

    // Assert
    verify(presenter, never()).setStatusInSync();
    verify(presenter).setStatusUnSync();
    verify(presenter, never()).setStatusError(anyString());
  }

  @Test
  public void updateStatus_inputSynced_shouldSetUnsynced() throws Exception {
    // Arrange
    doReturn(false).when(entity).isError();
    doReturn(VALID_SERVER_ID).when(entity).getId();

    // Act
    presenter.updateStatus(entity);

    // Assert
    verify(presenter).setStatusInSync();
    verify(presenter, never()).setStatusUnSync();
    verify(presenter, never()).setStatusError(anyString());
  }

  @Test
  public void updateStatus_inputError_shouldSetError() throws Exception {
    // Arrange
    doReturn(true).when(entity).isError();
    doReturn(NO_SERVER_ID).when(entity).getId();

    // Act
    presenter.updateStatus(entity);

    // Assert
    verify(presenter, never()).setStatusInSync();
    verify(presenter, never()).setStatusUnSync();
    verify(presenter).setStatusError(anyString());
  }

  @Test
  public void updateStatus_inputErrorWithServer_shouldSetError() throws Exception {
    // Arrange
    doReturn(true).when(entity).isError();
    doReturn(VALID_SERVER_ID).when(entity).getId();

    // Act
    presenter.updateStatus(entity);

    // Assert
    verify(presenter, never()).setStatusInSync();
    verify(presenter, never()).setStatusUnSync();
    verify(presenter).setStatusError(anyString());
  }

}