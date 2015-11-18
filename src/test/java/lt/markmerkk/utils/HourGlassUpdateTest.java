package lt.markmerkk.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/18/15.
 */
public class HourGlassUpdateTest {
  @Test public void testListenerCall() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.listener = mock(HourGlass.Listener.class);
    doReturn(1000L).when(glass).current();

    // Act
    glass.update();

    // Assert
    verify(glass.listener).onTick(any(Long.class),any(Long.class),any(Long.class));
  }

  @Test public void testDurationReport() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.listener = mock(HourGlass.Listener.class);
    glass.startMillis = 1000;
    glass.endMillis = 3000; // current end
    glass.lastTick = 0;
    doReturn(1000L).when(glass).current(); // increase end

    // Act
    glass.update();

    // Assert
    verify(glass.listener).onTick(1000, 4000, 3000);
  }

  @Test public void testDurationReport2() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.listener = mock(HourGlass.Listener.class);
    glass.startMillis = 1000;
    glass.endMillis = 10000;
    glass.lastTick = 0;
    doReturn(1000L).when(glass).current();

    // Act
    glass.update();

    // Assert
    verify(glass.listener).onTick(1000, 11000, 10000);
  }

  @Test public void testTimeIncrease() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.listener = mock(HourGlass.Listener.class);
    glass.startMillis = 1000;
    glass.endMillis = 10000;
    doReturn(1000L).when(glass).current();

    // Act
    // Assert
    glass.update();
    glass.update(); // Note, no change in current
    glass.update();
    glass.update();
    verify(glass.listener, atLeastOnce()).onTick(1000, 11000, 10000);
  }

  @Test public void testTimeIncrease2() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.listener = mock(HourGlass.Listener.class);
    glass.startMillis = 1000;
    glass.endMillis = 10000;

    // Act
    // Assert
    doReturn(1000L).when(glass).current();
    glass.update();
    verify(glass.listener).onTick(1000, 11000, 10000);
    doReturn(2000L).when(glass).current();
    glass.update();
    verify(glass.listener).onTick(1000, 12000, 11000);
    doReturn(3000L).when(glass).current();
    glass.update();
    verify(glass.listener).onTick(1000, 13000, 12000);
  }

  @Test public void testErrorWhenStartFails() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.listener = mock(HourGlass.Listener.class);
    glass.startMillis = -1;
    glass.endMillis = 10000;
    doReturn(1000L).when(glass).current();

    // Act
    // Assert
    glass.update();
    verify(glass.listener, never()).onTick(any(Long.class),any(Long.class),any(Long.class));
    verify(glass.listener).onError(HourGlass.Error.START);
  }

  @Test public void testErrorWhenEndFails() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.listener = mock(HourGlass.Listener.class);
    glass.startMillis = 1000;
    glass.endMillis = -1;
    doReturn(1000L).when(glass).current();

    // Act
    // Assert
    glass.update();
    verify(glass.listener, never()).onTick(any(Long.class), any(Long.class), any(Long.class));
    verify(glass.listener).onError(HourGlass.Error.END);
  }

  @Test public void testErrorWhenStartBiggerThanEnd() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.listener = mock(HourGlass.Listener.class);
    glass.startMillis = 1000;
    glass.endMillis = 500;
    doReturn(1000L).when(glass).current();

    // Act
    // Assert
    glass.update();
    verify(glass.listener, never()).onTick(any(Long.class), any(Long.class), any(Long.class));
    verify(glass.listener).onError(HourGlass.Error.DURATION);
  }

}