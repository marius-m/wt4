package lt.markmerkk.storage2.jobs;

import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import lt.markmerkk.storage2.database.interfaces.DBIndexable;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class QueryJobQueryTest {
  @Test public void testNoTableAnno() throws Exception {
    // Arrange
    QueryJob queryJob = new QueryJob<Mock1Empty>(Mock1Empty.class, mock(DBIndexable.class));

    // Act
    // Assert
    try {
      queryJob.query();
      fail("Should not return a query with no @Table");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not have @Table annotation!");
    }
  }

  //@Test public void testNoIndexSpecified() throws Exception {
  //  // Arrange
  //  QueryJob queryJob = new QueryJob<Mock3NoInterfaces>(Mock3NoInterfaces.class);
  //
  //  // Act
  //  // Assert
  //  try {
  //    queryJob.query();
  //    fail("Should not return a query with index value");
  //  } catch (IllegalArgumentException e) {
  //    assertThat(e).hasMessage("Provided class does not specify and index to look up to!");
  //  }
  //}

  @Test public void testValid1() throws Exception {
    // Arrange
    QueryJob queryJob = new QueryJob<Mock3>(Mock3.class, new Mock3("some_title", "some_param"));

    // Act
    String query = queryJob.query();

    // Assert
    assertThat(query).isEqualTo("SELECT * FROM mock3 WHERE title = 'some_title'");
  }
}