package lt.markmerkk.storage2.database;

import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;
import lt.markmerkk.storage2.jobs.CreateJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class DBMockExecutorProdTest {
  @Test public void shouldCreateTable() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    // Act
    // Assert
    executor.execute(new CreateJob<>(Mock3.class));
  }

  @Test public void shouldCreateRepeatedlyTable() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    // Act
    // Assert
    executor.execute(new CreateJobIfNeeded<>(Mock3.class));
    executor.execute(new CreateJobIfNeeded<>(Mock3.class));

    // todo: check somehow if this really works
  }

  @Test public void shouldInsertMock() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    // Act
    Mock4 mock4 =
        new Mock4(20L, 30L, "some_param", "some_title", "some_name");
    // Assert
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob(Mock4.class, mock4));

    // todo: check somehow if this really works
  }

  @Test public void shouldQueryMock3() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    Mock3 mock3 = new Mock3("some_title", "some_param");
    executor.execute(new CreateJobIfNeeded<>(Mock3.class));
    executor.execute(new InsertJob(Mock3.class, mock3));
    QueryJob<Mock3> queryJob = new QueryJob<Mock3>(Mock3.class);
    executor.execute(queryJob);
    Mock3 result = queryJob.result();

    // Assert
    System.out.println(result);
    assertThat(result.getTitle()).isEqualTo("some_title");
    assertThat(result.getParam()).isEqualTo("some_param");
  }

  @Test public void shouldQueryMock4() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    Mock4 mock = new Mock4(
        20L,
        30L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob(Mock4.class, mock));
    QueryJob<Mock4> queryJob = new QueryJob<Mock4>(Mock4.class);
    executor.execute(queryJob);
    Mock4 result = queryJob.result();

    // Assert
    System.out.println(result);
    assertThat(result.getTitle()).isEqualTo("some_title");
    assertThat(result.getName()).isEqualTo("some_name");
    assertThat(result.getParentParam()).isEqualTo("some_parent_param");
    assertThat(result.get_id()).isEqualTo(20L);
    assertThat(result.getId()).isEqualTo(30L);
  }

}