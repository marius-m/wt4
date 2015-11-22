package lt.markmerkk.storage2.jobs;

import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import lt.markmerkk.storage2.database.interfaces.DBIndexable;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class QueryJobInitTest {
  @Test public void testNullClass() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new QueryJob(null, mock(DBIndexable.class));
      fail("Should not create a job with invalid input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot create job without a class");
    }
  }

  @Test public void testNullEntity() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new QueryJob(Mock4.class, null);
      fail("Should not create a job with invalid input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot create job without an index");
    }
  }

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    new QueryJob<Mock4>(Mock4.class, mock(DBIndexable.class));
  }
}