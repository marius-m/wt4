package lt.markmerkk.storage2.jobs;

import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class QueryDistinctListJobQueryTest {

  @Test public void testNoTableAnno() throws Exception {
    // Arrange
    QueryDistinctListJob queryJob = new QueryDistinctListJob<Mock1Empty>(Mock1Empty.class, () -> "asdc");

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
    QueryDistinctListJob queryJob = new QueryDistinctListJob<Mock3>(Mock3.class, () -> null);

    // Act
    // Assert
    try {
      queryJob.query();
      fail("Should not return a query with no @Table");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Indexable not implemented!");
    }
  }


  @Test public void testValid2() throws Exception {
    // Arrange
    QueryDistinctListJob queryJob = new QueryDistinctListJob<Mock3>(Mock3.class, () -> "City");

    // Act
    String query = queryJob.query();

    // Assert
    assertThat(query).isEqualTo("SELECT DISTINCT City FROM mock3;");
  }

}