package lt.markmerkk.storage2.database;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoExtend;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;
import lt.markmerkk.storage2.jobs.CreateJob;
import lt.markmerkk.storage2.jobs.InsertJob;
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
    executor.execute(new CreateJob<>(Mock3NoExtend.class));
  }

  @Test public void shouldCreateRepeatedlyTable() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    // Act
    // Assert
    executor.execute(new CreateJobIfNeeded<>(Mock3NoExtend.class));
    executor.execute(new CreateJobIfNeeded<>(Mock3NoExtend.class));

    // todo: check somehow if this really works
  }

  @Test public void shouldInsertMock() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    // Act
    Mock4 mock4 =
        new Mock4("some_grandparent_id", "some_parent_id", "some_parent_param", "some_title",
            "some_param");
    // Assert
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob<Mock4>(Mock4.class, mock4));

    // todo: check somehow if this really works
  }

}