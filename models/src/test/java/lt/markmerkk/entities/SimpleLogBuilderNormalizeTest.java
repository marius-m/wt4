package lt.markmerkk.entities;

import lt.markmerkk.entities.SimpleLogBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 2/9/16.
 */
public class SimpleLogBuilderNormalizeTest {
  @Test
  public void test_inputDoubleQuotes_shouldNormalize() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.normalize("asdf\"asdf\"asdf");

    // Assert
    assertThat(out).isEqualTo("asdf\'asdf\'asdf");
  }

  @Test
  public void test_inputNormal_shouldReturnSame() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.normalize("asdfasdf");

    // Assert
    assertThat(out).isEqualTo("asdfasdf");
  }

  @Test
  public void test_inputNull_shouldReturnNull() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.normalize(null);

    // Assert
    assertThat(out).isEqualTo("");
  }

  @Test
  public void test_inputEmpty_shouldReturnEmpty() throws Exception {
    // Arrange
    SimpleLogBuilder builder = new SimpleLogBuilder();

    // Act
    String out = builder.normalize("");

    // Assert
    assertThat(out).isEqualTo("");
  }
}