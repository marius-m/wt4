package lt.markmerkk.storage2.jobs;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class CreateJobInitTest {
  @Test public void testNull() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new CreateJob<>(null);
      fail("Should not create a class with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Cannot create job without a class");
    }
  }

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    new CreateJob<>(Mock1.class);
  }

  private class Mock1 {}

}