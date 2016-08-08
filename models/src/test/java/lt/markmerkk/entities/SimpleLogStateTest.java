package lt.markmerkk.entities;

import lt.markmerkk.entities.SimpleLog;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/29/15.
 */
public class SimpleLogStateTest {
  @Test public void testUnknown() throws Exception {
    // Arrange
    SimpleLog log = new SimpleLog();

    // Act
    String imageUrl = log.getStateImageUrl();

    // Assert
    assertThat(imageUrl).isEqualTo("/yellow.png");
  }

  @Test public void testLocalDirty() throws Exception {
    // Arrange
    SimpleLog log = new SimpleLog();
    log.dirty = true;

    // Act
    String imageUrl = log.getStateImageUrl();

    // Assert
    assertThat(imageUrl).isEqualTo("/yellow.png");
  }

  @Test public void testServerClean() throws Exception {
    // Arrange
    SimpleLog log = new SimpleLog();
    log.id = 1234;
    log.dirty = false;

    // Act
    String imageUrl = log.getStateImageUrl();

    // Assert
    assertThat(imageUrl).isEqualTo("/green.png");
  }

  @Test public void testServerDirty() throws Exception {
    // Arrange
    SimpleLog log = new SimpleLog();
    log.id = 1234;
    log.dirty = true;

    // Act
    String imageUrl = log.getStateImageUrl();

    // Assert
    assertThat(imageUrl).isEqualTo("/yellow.png");
  }

  @Test public void testServerError() throws Exception {
    // Arrange
    SimpleLog log = new SimpleLog();
    log.dirty = false;
    log.id = 1234;
    log.error = true;

    // Act
    String imageUrl = log.getStateImageUrl();

    // Assert
    assertThat(imageUrl).isEqualTo("/red.png");
  }

  // This should never occur
  @Test public void testServerErrorDirty() throws Exception {
    // Arrange
    SimpleLog log = new SimpleLog();
    log.id = 1234;
    log.error = true;
    log.dirty = true;

    // Act
    String imageUrl = log.getStateImageUrl();

    // Assert
    assertThat(imageUrl).isEqualTo("/yellow.png");
  }

  @Test public void testServerDeleted() throws Exception {
    // Arrange
    SimpleLog log = new SimpleLog();
    log.id = 1234;
    log.deleted = true;

    // Act
    String imageUrl = log.getStateImageUrl();

    // Assert
    assertThat(imageUrl).isEqualTo("/gray.png");
  }

  // This option should never occur
  @Test public void testLocalDeleted() throws Exception {
    // Arrange
    SimpleLog log = new SimpleLog();
    log.deleted = true;

    // Act
    String imageUrl = log.getStateImageUrl();

    // Assert
    assertThat(imageUrl).isEqualTo("/gray.png");
  }

}