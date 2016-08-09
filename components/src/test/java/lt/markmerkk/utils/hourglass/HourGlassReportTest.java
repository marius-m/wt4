package lt.markmerkk.utils.hourglass;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by mariusmerkevicius on 11/19/15.
 */
public class HourGlassReportTest {
  @Test public void testStartWhenNotStarted() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(1000L).when(glass).current();
    doReturn(false).when(glass).isValid();
    glass.startMillis = -1;
    // Act
    // Assert
    assertThat(glass.reportStart().getMillis()).isEqualTo(1000);
  }

  @Test public void testStartWhenStarted() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(1000L).when(glass).current();
    doReturn(true).when(glass).isValid();
    glass.startMillis = 333;
    // Act
    // Assert
    assertThat(glass.reportStart().getMillis()).isEqualTo(333);
  }

  @Test public void testEndWhenNotStarted() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(1000L).when(glass).current();
    doReturn(false).when(glass).isValid();
    glass.endMillis = -1;
    // Act
    // Assert
    assertThat(glass.reportEnd().getMillis()).isEqualTo(1000);
  }

  @Test public void testEndWhenStarted() throws Exception {
    // Arrange
    HourGlass glass = spy(new HourGlass());
    doReturn(1000L).when(glass).current();
    doReturn(true).when(glass).isValid();
    glass.endMillis = 333;
    // Act
    // Assert
    assertThat(glass.reportEnd().getMillis()).isEqualTo(333);
  }
}