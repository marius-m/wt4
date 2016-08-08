package lt.markmerkk.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 2/9/16.
 */
public class TimeSplitPickCommentTest {
  @Test
  public void test_inputValid_shouldPick() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.removeStamp("15:20 >> comment");

    // Assert
    assertThat(out).isEqualTo("comment");
  }

  @Test
  public void test_inputValidInvalidSplit_shouldPick() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.removeStamp("15:20 > comment");

    // Assert
    assertThat(out).isEqualTo("15:20 > comment");
  }

  @Test
  public void test_inputValidNoSplit_shouldPick() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.removeStamp(" comment");

    // Assert
    assertThat(out).isEqualTo("comment");
  }

  @Test
  public void test_inputNull_shouldReturnNull() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.removeStamp(null);

    // Assert
    assertThat(out).isEqualTo(null);
  }

  @Test
  public void test_inputempty_shouldReturnEmpty() throws Exception {
    // Arrange
    // Act
    String out = TimeSplit.removeStamp("");

    // Assert
    assertThat(out).isEqualTo(null);
  }
}