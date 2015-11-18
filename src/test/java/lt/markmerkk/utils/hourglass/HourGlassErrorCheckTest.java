package lt.markmerkk.utils.hourglass;

import lt.markmerkk.utils.hourglass.HourGlass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/19/15.
 */
public class HourGlassErrorCheckTest {
  @Test public void testCorrect() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();
    glass.startMillis = 100;
    glass.endMillis = 10000;
    // Act
    // Assert
    assertThat(glass.isValid()).isTrue();
  }

  @Test public void testIncorrectStart() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();
    glass.startMillis = -1;
    glass.endMillis = 10000;
    // Act
    // Assert
    assertThat(glass.isValid()).isFalse();
  }

  @Test public void testIncorrectEnd() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();
    glass.startMillis = 100;
    glass.endMillis = -1;
    // Act
    // Assert
    assertThat(glass.isValid()).isFalse();
  }

  @Test public void testIncorrectTooLow() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();
    glass.startMillis = 100;
    glass.endMillis = 50;
    // Act
    // Assert
    assertThat(glass.isValid()).isFalse();
  }
}