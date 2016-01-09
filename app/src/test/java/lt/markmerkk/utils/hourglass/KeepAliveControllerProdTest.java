package lt.markmerkk.utils.hourglass;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 1/9/16.
 */
// Integration tests
@Ignore
public class KeepAliveControllerProdTest {

  @Mock
  KeepAliveController.Listener listener;
  private KeepAliveController controller;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    controller = new KeepAliveController();
    controller.delay = 1000;
    controller.setListener(listener);
  }

  @Test
  public void init_validStart_shouldTick3Times() throws Exception {
    // Arrange
    controller.setListener(listener);

    // Act
    controller.start();

    // Assert
    Thread.sleep(3000);
    verify(listener, times(4)).onUpdate();

  }

  @Test
  public void init_validNoStart_shouldNotInvoke() throws Exception {
    // Arrange
    controller.setListener(listener);

    // Act
    //controller.start();

    // Assert
    Thread.sleep(3000);
    verify(listener, never()).onUpdate();
  }

  @Test
  public void init_validStartStop_shouldNotInvoke() throws Exception {
    // Arrange
    controller.setListener(listener);

    // Act
    controller.start();
    controller.stop();

    // Assert
    Thread.sleep(3000);
    verify(listener, never()).onUpdate();
  }
}