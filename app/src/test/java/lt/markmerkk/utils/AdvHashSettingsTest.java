package lt.markmerkk.utils;

import java.util.Properties;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 12/21/15.
 */
public class AdvHashSettingsTest {
  @Test public void testValidInput() throws Exception {
    // Arrange
    AdvHashSettings settings = new AdvHashSettings();
    Properties properties = new Properties();

    // Act
    settings.set("valid_key", "valid_value");
    settings.set("valid_key1", "valid_value1");
    settings.set("valid_key2", "valid_value2");
    settings.onSave(properties);
    settings.save();

    // Assert
    assertThat(properties.get("valid_key")).isEqualTo("dmFsaWRfdmFsdWU=");
    assertThat(properties.get("valid_key1")).isEqualTo("dmFsaWRfdmFsdWUx");
    assertThat(properties.get("valid_key2")).isEqualTo("dmFsaWRfdmFsdWUy");
  }

  @Test public void testValidOutput() throws Exception {
    // Arrange
    AdvHashSettings settings = new AdvHashSettings();

    // Act
    Properties outputProperties = new Properties();
    outputProperties.put("valid_key", "dmFsaWRfdmFsdWU=");
    outputProperties.put("valid_key1", "dmFsaWRfdmFsdWUx");
    outputProperties.put("valid_key2", "dmFsaWRfdmFsdWUy");
    settings.onLoad(outputProperties);

    // Assert
    assertThat(settings.get("valid_key")).isEqualTo("valid_value");
    assertThat(settings.get("valid_key1")).isEqualTo("valid_value1");
    assertThat(settings.get("valid_key2")).isEqualTo("valid_value2");
  }

  @Test public void testMalformOutput() throws Exception {
    // Arrange
    AdvHashSettings settings = new AdvHashSettings();

    // Act
    Properties outputProperties = new Properties();
    outputProperties.put("valid_key", "aaa"); // Invalid, cant be decoded
    settings.onLoad(outputProperties);

    // Assert
    assertThat(settings.get("valid_key")).isNotNull();
    assertThat(settings.keyValues.size()).isEqualTo(1);
  }

  @Test public void testEmptyOutput() throws Exception {
    // Arrange
    AdvHashSettings settings = new AdvHashSettings();

    // Act
    Properties outputProperties = new Properties();
    outputProperties.put("valid_key", ""); // Invalid, cant be decoded
    settings.onLoad(outputProperties);

    // Assert
    assertThat(settings.get("valid_key")).isNull();
    assertThat(settings.keyValues.size()).isZero();
  }

  @Test public void testInputNullValue() throws Exception {
    // Arrange
    AdvHashSettings settings = new AdvHashSettings();
    Properties properties = new Properties();

    // Act
    settings.set("valid_key", null);
    settings.onSave(properties);
    settings.save();

    // Assert
    assertThat(properties.get("valid_key")).isNull();
    assertThat(properties.size()).isZero();
  }

  @Test public void testInputNullKey() throws Exception {
    // Arrange
    AdvHashSettings settings = new AdvHashSettings();
    Properties properties = new Properties();

    // Act
    settings.set(null, "valid_value");
    settings.onSave(properties);
    settings.save();

    // Assert
    assertThat(properties.size()).isZero();
  }
}