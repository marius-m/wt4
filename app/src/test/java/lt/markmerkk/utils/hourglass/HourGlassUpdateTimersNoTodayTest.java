package lt.markmerkk.utils.hourglass;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/18/15.
 */
public class HourGlassUpdateTimersNoTodayTest {

  //region Method init

  @Test(expected = IllegalArgumentException.class)
  public void testNullInputStart() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();

    // Act
    // Assert
    glass.updateTimers(null, "asf");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInputEnd() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();

    // Act
    // Assert
    glass.updateTimers("asdf", null);
  }

  //endregion

  @Test public void testIncorrectStart() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();

    // Act
    glass.updateTimers("222", "2015-05-02 09:30");

    // Assert
    assertThat(glass.startMillis).isEqualTo(-1);
  }

  @Test public void testIncorrectEnd() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();

    // Act
    glass.updateTimers("2015-05-02 09:20", "asf");

    // Assert
    assertThat(glass.endMillis).isEqualTo(-1);
  }

  @Test public void testCorrect() throws Exception {
    // Arrange
    HourGlass glass = new HourGlass();

    // Act
    glass.updateTimers("2015-05-02 09:20", "2015-05-02 09:40");

    // Assert
    assertThat(glass.startMillis).isNotEqualTo(-1);
    assertThat(glass.endMillis).isNotEqualTo(-1);
  }

}