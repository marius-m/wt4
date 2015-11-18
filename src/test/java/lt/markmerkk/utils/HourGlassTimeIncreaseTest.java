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
public class HourGlassTimeIncreaseTest {
  @Test public void testNormalIncrease() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(3000L).when(glass).current();
    glass.lastTick = 1000L;

    // Act
    // Assert
    assertThat(glass.calcTimeIncrease()).isEqualTo(2000);
    assertThat(glass.lastTick).isEqualTo(3000);
  }

  @Test public void testNormalIncrease2() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(10000L).when(glass).current();
    glass.lastTick = 500;

    // Act
    // Assert
    assertThat(glass.calcTimeIncrease()).isEqualTo(9500);
    assertThat(glass.lastTick).isEqualTo(10000);
  }

  @Test public void testAbnormalLastTick() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(10000L).when(glass).current();
    glass.lastTick = -1;

    // Act
    // Assert
    assertThat(glass.calcTimeIncrease()).isEqualTo(0);
    assertThat(glass.lastTick).isEqualTo(-1);
  }

  @Test public void testAbnormalCurrent() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(-1L).when(glass).current();
    glass.lastTick = 500;

    // Act
    // Assert
    assertThat(glass.calcTimeIncrease()).isEqualTo(0);
    assertThat(glass.lastTick).isEqualTo(500);
  }

  @Test public void testLastTickBiggerThanCurrent() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(100L).when(glass).current();
    glass.lastTick = 500;

    // Act
    // Assert
    assertThat(glass.calcTimeIncrease()).isEqualTo(0);
    assertThat(glass.lastTick).isEqualTo(500);
  }

}