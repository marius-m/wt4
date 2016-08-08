package lt.markmerkk.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 2/10/16.
 */
public class TimeSplitAddStampTest {
  @Test
  public void test_inputValid_shouldAddComment() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.addStamp(1000, 2000, "Simple comment");

    // Assert
    assertThat(out).isEqualTo("03:00 - 03:00 >> Simple comment");
  }

  @Test
  public void test_inputValidReoccuring_shouldAddComment() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.addStamp(1000, 2000, "Simple comment"); // 03:00 - 03:00
    out = TimeSplit.addStamp(1000000, 2000000, out); // 03:16 - 03:33
    out = TimeSplit.addStamp(1111111, 6666666, out); // 03:18 - 04:51

    // Assert
    assertThat(out).isEqualTo("03:18 - 04:51 >> Simple comment");
  }

  @Test
  public void test_inputValid2_shouldAddComment() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.addStamp(1000, 2000, "a");

    // Assert
    assertThat(out).isEqualTo("03:00 - 03:00 >> a");
  }

  @Test
  public void test_inputNullComment_shouldBeNull() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.addStamp(1000, 2000, null);

    // Assert
    assertThat(out).isNull();
  }

  @Test
  public void test_inputEmptyComment_shouldBeNull() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.addStamp(1000, 2000, "");

    // Assert
    assertThat(out).isNull();
  }

  @Test
  public void test_inputMalformStart_shouldBeValid() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.addStamp(-200, 2000, "asdf");

    // Assert
    assertThat(out).isEqualTo("02:59 - 03:00 >> asdf");
  }

  @Test
  public void test_inputMalformEnd_shouldBeValid() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.addStamp(1000, -222, "asdf");

    // Assert
    assertThat(out).isEqualTo("03:00 - 02:59 >> asdf");
  }
}