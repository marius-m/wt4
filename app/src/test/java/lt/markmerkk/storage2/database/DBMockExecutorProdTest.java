package lt.markmerkk.storage2.database;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;
import lt.markmerkk.storage2.jobs.CreateJob;
import org.junit.Test;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class DBMockExecutorProdTest {
  @Test public void shouldCreateTable() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    // Act
    // Assert
    executor.execute(new CreateJob<>(Mock1.class));
  }

  @Test public void shouldCreateRepeatedlyTable() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    // Act
    // Assert
    executor.execute(new CreateJobIfNeeded<>(Mock1.class));
    executor.execute(new CreateJobIfNeeded<>(Mock1.class));
  }

  @Table(name = "mock1")
  private class Mock1 {
    @Column(value = FieldType.TEXT) String title;
    @Column(value = FieldType.TEXT) String name;
  }

}