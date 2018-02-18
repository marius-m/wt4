package lt.markmerkk.ui.update;

import lt.markmerkk.entities.SimpleLog;
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
    doReturn(true).when(entity).canEdit();

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
    doReturn(false).when(entity).canEdit();

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
    doReturn("valid_error_msg").when(entity).getErrorMessage();
    doReturn(true).when(entity).canEdit();

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
    doReturn("valid_error_msg").when(entity).getErrorMessage();
    doReturn(false).when(entity).canEdit();

    // Act
    presenter.updateStatus(entity);

    // Assert
    verify(presenter, never()).setStatusInSync();
    verify(presenter, never()).setStatusUnSync();
    verify(presenter).setStatusError(anyString());
  }

}