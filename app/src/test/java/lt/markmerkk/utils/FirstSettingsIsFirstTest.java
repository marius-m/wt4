package lt.markmerkk.utils;

import java.util.Properties;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 2/7/16.
 */
public class FirstSettingsIsFirstTest {
  @Test
  public void test_init_shouldTrue() throws Exception {
    // Arrange
    FirstSettings firstSettings = new FirstSettings();

    // Act
    boolean first = firstSettings.isFirst();

    // Assert
    assertThat(first).isTrue();
  }

  @Test
  public void test_inputLoadPropertyDoesNotExist_shouldReturnTrue() throws Exception {
    // Arrange
    FirstSettings firstSettings = new FirstSettings();
    Properties properties = mock(Properties.class);
    doReturn("true").when(properties).getProperty(anyString(), anyString());

    // Act
    firstSettings.onLoad(properties);
    boolean isFirst = firstSettings.isFirst();

    // Assert
    assertThat(isFirst).isTrue();
  }

  @Test
  public void test_inputLoadPropertyReturnNull_shouldReturnTrue() throws Exception {
    // Arrange
    FirstSettings firstSettings = new FirstSettings();
    Properties properties = mock(Properties.class);
    doReturn(null).when(properties).getProperty(anyString(), anyString());

    // Act
    firstSettings.onLoad(properties);
    boolean isFirst = firstSettings.isFirst();

    // Assert
    assertThat(isFirst).isTrue();
  }

  @Test
  public void test_inputLoadPropertyReturnMalformed_shouldReturnTrue() throws Exception {
    // Arrange
    FirstSettings firstSettings = new FirstSettings();
    Properties properties = mock(Properties.class);
    doReturn("asdf").when(properties).getProperty(anyString(), anyString());

    // Act
    firstSettings.onLoad(properties);
    boolean isFirst = firstSettings.isFirst();

    // Assert
    assertThat(isFirst).isTrue();
  }

  @Test
  public void test_inputLoadPropertyReturnFalse_shouldReturnFalse() throws Exception {
    // Arrange
    FirstSettings firstSettings = new FirstSettings();
    Properties properties = mock(Properties.class);
    doReturn("false").when(properties).getProperty(anyString(), anyString());

    // Act
    firstSettings.onLoad(properties);
    boolean isFirst = firstSettings.isFirst();

    // Assert
    assertThat(isFirst).isFalse();
  }

}