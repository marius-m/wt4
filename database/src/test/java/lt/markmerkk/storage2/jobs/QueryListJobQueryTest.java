package lt.markmerkk.storage2.jobs;

import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class QueryListJobQueryTest {
  @Test public void testNoTableAnno() throws Exception {
    // Arrange
    QueryListJob queryJob = new QueryListJob<Mock1Empty>(Mock1Empty.class);

    // Act
    // Assert
    try {
      queryJob.query();
      fail("Should not return a query with no @Table");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not have @Table annotation!");
    }
  }

  @Test public void testIndexableWrong() throws Exception {
    // Arrange
    QueryListJob queryJob = new QueryListJob<Mock3>(Mock3.class, () -> null);

    // Act
    // Assert
    try {
      queryJob.query();
      fail("Should not return a query with no @Table");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Indexable not implemented!");
    }
  }

  @Test public void testValid1() throws Exception {
    // Arrange
    QueryListJob queryJob = new QueryListJob<Mock3>(Mock3.class);

    // Act
    String query = queryJob.query();

    // Assert
    assertThat(query).isEqualTo("SELECT * FROM mock3;");
  }

  @Test public void testValid2() throws Exception {
    // Arrange
    QueryListJob queryJob = new QueryListJob<Mock3>(Mock3.class, () -> "title = 'some_title'");

    // Act
    String query = queryJob.query();

    // Assert
    assertThat(query).isEqualTo("SELECT * FROM mock3 WHERE title = 'some_title';");
  }

}