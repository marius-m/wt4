package lt.markmerkk.storage2.jobs;

import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class InsertJobInitTest {
  @Test public void testNullClass() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new InsertJob<>(null, mock(Mock4.class));
      fail("Should not create a class with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Cannot create job without a class");
    }
  }

  @Test public void testNullEntity() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new InsertJob<>(Mock4.class, null);
      fail("Should not create a class with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Cannot create job without an instance");
    }
  }

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    new InsertJob<Mock4>(Mock4.class, mock(Mock4.class));
  }

}