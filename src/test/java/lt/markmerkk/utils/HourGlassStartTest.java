package lt.markmerkk.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by mariusmerkevicius on 11/18/15.
 */
public class HourGlassStartTest {
  @Test public void testStartingParameters() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(1000L).when(glass).current();

    // Act
    glass.start();

    // Assert
    assertThat(glass.pauseReport).isFalse();
    assertThat(glass.startMillis).isEqualTo(1000);
    assertThat(glass.endMillis).isEqualTo(1000);
    assertThat(glass.lastTick).isEqualTo(1000);
  }
}