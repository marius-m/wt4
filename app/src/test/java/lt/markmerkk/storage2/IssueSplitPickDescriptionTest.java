package lt.markmerkk.storage2;

import java.util.Map;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
public class IssueSplitPickDescriptionTest {
  @Test
  public void test_unsplittablePhrase_shouldNotSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart("asdf", IssueSplit.DESCRIPTION_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("asdf");
  }

  @Test
  public void test_validPhrase_shouldSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart("TT-12 : asdf", IssueSplit.DESCRIPTION_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("asdf");
  }

  @Test
  public void test_noSpacesPhrase_shouldSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart("TT-12:asdf", IssueSplit.DESCRIPTION_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("asdf");
  }

  @Test
  public void test_unnecessarySpacesPhrase_shouldSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart("TT-12a :   asdf    ", IssueSplit.DESCRIPTION_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("asdf");
  }

  @Test
  public void test_null_shouldReturnEmpty() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart(null, IssueSplit.DESCRIPTION_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("");
  }

  @Test
  public void test_empty_shouldReturnEmpty() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    String result = splitter.pickPart(null, IssueSplit.DESCRIPTION_REGEX);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("");
  }

}