package lt.markmerkk.storage2;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
public class IssueSplitPickKeyTest {
  @Test
  public void test_unsplittablePhrase_shouldNotSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart("tt-123", IssueSplit.KEY_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("tt-123");
  }

  @Test
  public void test_splittablePhrase_shouldSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart("tt-12:asdf", IssueSplit.KEY_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("tt-12");
  }

  @Test
  public void test_splittablePhrase2_shouldSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart(":asdf", IssueSplit.KEY_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(":asdf");
  }

  @Test
  public void test_unsplittablePhrase2_shouldNotSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart("tt-12asdf", IssueSplit.KEY_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("tt-12asdf");
  }

  @Test
  public void test_null_shouldReturnEmpty() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart(null, IssueSplit.KEY_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("");
  }

  @Test
  public void test_empty_shouldReturnEmpty() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart(null, IssueSplit.KEY_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("");
  }
}