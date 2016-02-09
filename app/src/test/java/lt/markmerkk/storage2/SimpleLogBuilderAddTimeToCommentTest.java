package lt.markmerkk.storage2;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 2/9/16.
 */
public class SimpleLogBuilderAddTimeToCommentTest {
  @Test
  public void test_inputValid_shouldAddComment() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.addTimeToComment(1000, 2000, "Simple comment");

    // Assert
    assertThat(out).isEqualTo("03:00 - 03:00 >> Simple comment");
  }

  @Test
  public void test_inputValid2_shouldAddComment() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.addTimeToComment(1000, 2000, "a");

    // Assert
    assertThat(out).isEqualTo("03:00 - 03:00 >> a");
  }

  @Test
  public void test_inputNullComment_shouldBeNull() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.addTimeToComment(1000, 2000, null);

    // Assert
    assertThat(out).isNull();
  }

  @Test
  public void test_inputEmptyComment_shouldBeNull() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.addTimeToComment(1000, 2000, "");

    // Assert
    assertThat(out).isNull();
  }

  @Test
  public void test_inputMalformStart_shouldBeValid() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.addTimeToComment(-200, 2000, "asdf");

    // Assert
    assertThat(out).isEqualTo("02:59 - 03:00 >> asdf");
  }

  @Test
  public void test_inputMalformEnd_shouldBeValid() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.addTimeToComment(1000, -222, "asdf");

    // Assert
    assertThat(out).isEqualTo("03:00 - 02:59 >> asdf");
  }
}