package lt.markmerkk.utils;

import java.util.Timer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by mariusmerkevicius on 11/18/15.
 */
public class HourGlassStopTest {
  @Test public void testStopParameters() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    glass.state = HourGlass.State.RUNNING;
    glass.lastTick = 100;
    glass.timer = mock(Timer.class);
    doReturn(1000L).when(glass).current();

    // Act
    glass.stop();

    // Assert
    assertThat(glass.pauseReport).isFalse();
    assertThat(glass.startMillis).isEqualTo(0);
    assertThat(glass.endMillis).isEqualTo(0);
    assertThat(glass.lastTick).isEqualTo(-1);
  }

}