package lt.markmerkk.storage2;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 12/13/15.
 */
public class BasicLogStorageRegisterTest {

  @Test public void testValidRegister() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage();
    ILoggerListener listener = mock(ILoggerListener.class);

    // Act
    storage.register(listener);

    // Assert
    assertThat(storage.listeners.size()).isEqualTo(1);
    assertThat(storage.listeners.get(0)).isEqualTo(listener);
  }

  @Test public void testValidUnregister() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage();
    ILoggerListener listener = mock(ILoggerListener.class);

    // Act
    storage.register(listener);

    // Assert
    assertThat(storage.listeners.size()).isEqualTo(1);
    assertThat(storage.listeners.get(0)).isEqualTo(listener);

    // Act
    storage.unregister(listener);

    // Assert
    assertThat(storage.listeners.size()).isEqualTo(0);
  }

  @Test public void testUnregisterNoSuchListener() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage();
    ILoggerListener listener = mock(ILoggerListener.class);

    // Act
    storage.unregister(listener);

    // Assert
    assertThat(storage.listeners.size()).isEqualTo(0);
  }

  @Test public void testRegisterNull() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage();
    ILoggerListener listener = mock(ILoggerListener.class);

    // Act
    storage.register(null);

    // Assert
    assertThat(storage.listeners.size()).isEqualTo(0);
  }

  @Test public void testUnregisterNull() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage();
    ILoggerListener listener = mock(ILoggerListener.class);

    // Act
    storage.unregister(null);

    // Assert
    assertThat(storage.listeners.size()).isEqualTo(0);
  }
}