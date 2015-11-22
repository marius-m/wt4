package lt.markmerkk.storage2.jobs;

import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class QueryListJobInitTest {
  @Test public void testNull() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new QueryListJob<>(null);
      fail("Should not create a job without a class model input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot create job without a class");
    }
  }

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    new QueryListJob<Mock4>(Mock4.class);
  }
}