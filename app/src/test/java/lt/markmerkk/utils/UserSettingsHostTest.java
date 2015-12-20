package lt.markmerkk.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 12/21/15.
 */
public class UserSettingsHostTest {
  @Test public void testNoInput() throws Exception {
    // Arrange
    UserSettings settings = new UserSettings();

    // Act
    String var = settings.getHost();

    // Assert
    assertThat(var).isEmpty();
  }

  @Test public void testValidLoad() throws Exception {
    // Arrange
    UserSettings settings = spy(new UserSettings());
    settings.settings = spy(settings.settings);

    // Act
    settings.onStart();

    // Assert
    verify(settings.settings).load();
    verify(settings.settings).get(settings.HOST);
  }

  @Test public void testValidSave() throws Exception {
    // Arrange
    UserSettings settings = spy(new UserSettings());
    settings.settings = spy(settings.settings);

    // Act
    settings.onStart();
    settings.setHost("valid_host");
    settings.onStop();

    // Assert
    verify(settings.settings).set(eq(settings.HOST), eq("valid_host"));
    verify(settings.settings).save();
  }

  @Test public void testInvalidSaveEmpty() throws Exception {
    // Arrange
    UserSettings settings = spy(new UserSettings());
    settings.settings = spy(settings.settings);

    // Act
    settings.onStart();
    settings.setHost("");
    settings.onStop();

    // Assert
    verify(settings.settings).set(eq(settings.HOST), eq(""));
    verify(settings.settings).save();
  }

  @Test public void testInvalidSaveNull() throws Exception {
    // Arrange
    UserSettings settings = spy(new UserSettings());
    settings.settings = spy(settings.settings);

    // Act
    settings.onStart();
    settings.setHost(null);
    settings.onStop();

    // Assert
    verify(settings.settings).set(eq(settings.HOST), eq(""));
    verify(settings.settings).save();
  }
}