package lt.markmerkk.entities;

import java.util.Map;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
public class IssueSplitSplitTest {
  @Test
  public void test_simpleValidPhrase_shouldNotSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    Map<String, String> result = splitter.split("asdf");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(IssueSplit.KEY_KEY)).isEqualTo("asdf");
    assertThat(result.get(IssueSplit.DESCRIPTION_KEY)).isEqualTo("asdf");
  }

  @Test
  public void test_simpleValidPhrase2_shouldNotSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    Map<String, String> result = splitter.split("tt-12:asdf");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(IssueSplit.KEY_KEY)).isEqualTo("tt-12");
    assertThat(result.get(IssueSplit.DESCRIPTION_KEY)).isEqualTo("asdf");
  }

  @Test
  public void test_simpleValidPhrase3_shouldNotSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    Map<String, String> result = splitter.split("tt12:asdf");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(IssueSplit.KEY_KEY)).isEqualTo("tt12");
    assertThat(result.get(IssueSplit.DESCRIPTION_KEY)).isEqualTo("asdf");
  }

  @Test
  public void test_simpleValidPhrase4_shouldNotSplit() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    Map<String, String> result = splitter.split(":asdf");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(IssueSplit.KEY_KEY)).isEqualTo(":asdf");
    assertThat(result.get(IssueSplit.DESCRIPTION_KEY)).isEqualTo("asdf");
  }

  @Test
  public void test_nullPhrase_shouldReturnEmpty() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    Map<String, String> result = splitter.split(null);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(IssueSplit.KEY_KEY)).isEqualTo("");
    assertThat(result.get(IssueSplit.DESCRIPTION_KEY)).isEqualTo("");
  }

  @Test
  public void test_emptyPhrase_shouldReturnEmpty() throws Exception {
    // Arrange
    IssueSplit splitter = new IssueSplit();

    // Act
    Map<String, String> result = splitter.split("");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(IssueSplit.KEY_KEY)).isEqualTo("");
    assertThat(result.get(IssueSplit.DESCRIPTION_KEY)).isEqualTo("");
  }
}