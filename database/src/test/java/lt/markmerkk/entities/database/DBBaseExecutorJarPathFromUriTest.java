package lt.markmerkk.entities.database;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 3/6/16.
 */
public class DBBaseExecutorJarPathFromUriTest {
  @Test
  public void test_inputValid_shouldExtract() throws Exception {
    // Arrange
    // Act

    String result = DBBaseExecutor.jarPathFromUri("jar:file:/Users/mariusmerkevicius/Projects/wt4/app/build/distributions/WT4.app/Contents/Java/4.jar!/changelog_1.xml");
    // Assert

    assertThat(result).isEqualTo("/Users/mariusmerkevicius/Projects/wt4/app/build/distributions/WT4.app/Contents/Java/4.jar");
  }

  @Test
  public void test_inputNoJarPrefix_shouldExtract() throws Exception {
    // Arrange
    // Act

    String result = DBBaseExecutor.jarPathFromUri("file:/Users/mariusmerkevicius/Projects/wt4/app/build/distributions/WT4.app/Contents/Java/4.jar!/changelog_1.xml");
    // Assert

    assertThat(result).isEqualTo("/Users/mariusmerkevicius/Projects/wt4/app/build/distributions/WT4.app/Contents/Java/4.jar");
  }

  @Test
  public void test_inputNoProperPostfix_shouldExtract() throws Exception {
    // Arrange
    // Act

    String result = DBBaseExecutor.jarPathFromUri("jar:file:/Users/mariusmerkevicius/Projects/wt4/app/build/distributions/WT4.app/Contents/Java/4.jar");
    // Assert

    assertThat(result).isEqualTo("/Users/mariusmerkevicius/Projects/wt4/app/build/distributions/WT4.app/Contents/Java/4.jar");
  }

  @Test
  public void test_inputMalformed_shouldExtract() throws Exception {
    // Arrange
    // Act

    String result = DBBaseExecutor.jarPathFromUri("asdf");
    // Assert

    assertThat(result).isEqualTo("asdf");
  }

  @Test
  public void test_inputNull_shouldThrow() throws Exception {
    // Arrange
    // Act

    String result = null;
    try {
      result = DBBaseExecutor.jarPathFromUri(null);
      fail("Should fail");
    } catch (Exception e) {
      assertThat(e).hasMessage("Error extracting jar!");
    }
    // Assert
    assertThat(result).isNull();
  }

}