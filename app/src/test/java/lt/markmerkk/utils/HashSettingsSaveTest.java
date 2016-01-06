package lt.markmerkk.utils;

import java.util.Properties;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 12/21/15.
 */
public class HashSettingsSaveTest {
  @Test public void testValidInput() throws Exception {
    // Arrange
    HashSettings settings = new HashSettings();
    Properties properties = new Properties();

    // Act
    settings.set("valid_key", "valid_value");
    settings.set("valid_key1", "valid_value1");
    settings.set("valid_key2", "valid_value2");
    settings.onSave(properties);
    settings.save();

    // Assert
    assertThat(properties.get("valid_key")).isEqualTo("valid_value");
    assertThat(properties.get("valid_key1")).isEqualTo("valid_value1");
    assertThat(properties.get("valid_key2")).isEqualTo("valid_value2");
  }

  @Test public void testValidOutput() throws Exception {
    // Arrange
    HashSettings settings = new HashSettings();

    // Act
    Properties outputProperties = new Properties();
    outputProperties.put("valid_key", "valid_value");
    outputProperties.put("valid_key1", "valid_value1");
    outputProperties.put("valid_key2", "valid_value2");
    settings.onLoad(outputProperties);

    // Assert
    assertThat(settings.get("valid_key")).isEqualTo("valid_value");
    assertThat(settings.get("valid_key1")).isEqualTo("valid_value1");
    assertThat(settings.get("valid_key2")).isEqualTo("valid_value2");
  }

  // Cant possibly be done!

  //@Test public void testInvalidOutputNullValue() throws Exception {
  //  // Arrange
  //  HashSettings settings = new HashSettings();
  //
  //  // Act
  //  Properties outputProperties = new Properties();
  //  outputProperties.put("valid_key", null);
  //  settings.onLoad(outputProperties);
  //
  //  // Assert
  //  assertThat(settings.keyValues.size()).isZero();
  //}

  // Cant possibly be done!

  //@Test public void testInvalidOutputNullKey() throws Exception {
  //  // Arrange
  //  HashSettings settings = new HashSettings();
  //
  //  // Act
  //  Properties outputProperties = new Properties();
  //  outputProperties.put(null, "valid_value");
  //  settings.onLoad(outputProperties);
  //
  //  // Assert
  //  assertThat(settings.keyValues.size()).isZero();
  //}

  @Test public void testInputNullValue() throws Exception {
    // Arrange
    HashSettings settings = new HashSettings();
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
    HashSettings settings = new HashSettings();
    Properties properties = new Properties();

    // Act
    settings.set(null, "valid_value");
    settings.onSave(properties);
    settings.save();

    // Assert
    assertThat(properties.size()).isZero();
  }
}