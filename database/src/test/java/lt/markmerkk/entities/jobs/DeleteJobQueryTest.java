package lt.markmerkk.entities.jobs;

import lt.markmerkk.entities.database.helpers.entities.Mock1Empty;
import lt.markmerkk.entities.database.helpers.entities.Mock3;
import lt.markmerkk.entities.database.interfaces.DBIndexable;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DeleteJobQueryTest {
  @Test public void testNoTableAnno() throws Exception {
    // Arrange
    DeleteJob queryJob = new DeleteJob(Mock1Empty.class, mock(DBIndexable.class));

    // Act
    // Assert
    try {
      queryJob.query();
      fail("Should not return a query with no @Table");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not have @Table annotation!");
    }
  }

  @Test public void testValid1() throws Exception {
    // Arrange
    DeleteJob queryJob = new DeleteJob(Mock3.class, new Mock3("some_title", "some_param"));

    // Act
    String query = queryJob.query();

    // Assert
    assertThat(query).isEqualTo("DELETE FROM mock3 WHERE title = 'some_title'");
  }
}