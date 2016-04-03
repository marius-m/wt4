package lt.markmerkk;

import java.util.Properties;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 4/3/16.
 */
public class TranslationGetStringTest {

  public static final String VALID_KEY = "valid_key";
  public static final String VALID_VALUE = "valid_value";
  public static final String INVALID_KEY = "invalid_key";

  @Test
  public void test_inputValid_shouldReturnString() throws Exception {
    // Arrange
    final Properties properties = mock(Properties.class);
    Translation translation = new Translation() {
      @Override
      Properties initTranslations() {
        return properties;
      }
    };

    doReturn(VALID_VALUE).when(properties).getProperty(VALID_KEY);
    doReturn(true).when(properties).containsKey(VALID_KEY);
    doReturn(false).when(properties).containsKey(INVALID_KEY);

    // Act
    String result = translation.getString(VALID_KEY);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(VALID_VALUE);
  }

  @Test
  public void test_inputInvalidString_shouldReturnUntranslated() throws Exception {
    // Arrange
    final Properties properties = mock(Properties.class);
    Translation translation = new Translation() {
      @Override
      Properties initTranslations() {
        return properties;
      }
    };

    doReturn(VALID_VALUE).when(properties).getProperty(VALID_KEY);
    doReturn(true).when(properties).containsKey(VALID_KEY);
    doReturn(false).when(properties).containsKey(INVALID_KEY);

    // Act
    String result = translation.getString(INVALID_KEY);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("untranslated_"+INVALID_KEY);
  }

  @Test
  public void test_inputNull_shouldReturnUntranslated() throws Exception {
    // Arrange
    final Properties properties = mock(Properties.class);
    Translation translation = new Translation() {
      @Override
      Properties initTranslations() {
        return properties;
      }
    };

    doReturn(VALID_VALUE).when(properties).getProperty(VALID_KEY);
    doReturn(true).when(properties).containsKey(VALID_KEY);
    doReturn(false).when(properties).containsKey(INVALID_KEY);

    // Act
    String result = translation.getString(null);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("untranslated");
  }
}