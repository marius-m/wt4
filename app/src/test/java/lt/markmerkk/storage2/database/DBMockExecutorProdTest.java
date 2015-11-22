package lt.markmerkk.storage2.database;

import java.util.List;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import lt.markmerkk.storage2.database.interfaces.DBIndexable;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;
import lt.markmerkk.storage2.jobs.CreateJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import lt.markmerkk.storage2.jobs.QueryListJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
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
    QueryJob<Mock3> queryJob = new QueryJob<Mock3>(Mock3.class, () -> "title = 'some_title'");
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
    QueryJob<Mock4> queryJob = new QueryJob<Mock4>(Mock4.class, () -> "_id = 20");
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

  @Test public void shouldQueryListMock4() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    Mock4 mock1 = new Mock4(
        20L,
        30L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    Mock4 mock2 = new Mock4(
        21L,
        31L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    Mock4 mock3 = new Mock4(
        22L,
        32L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob(Mock4.class, mock1));
    executor.execute(new InsertJob(Mock4.class, mock2));
    executor.execute(new InsertJob(Mock4.class, mock3));
    QueryListJob<Mock4> queryListJob = new QueryListJob<Mock4>(Mock4.class);
    executor.execute(queryListJob);
    List<Mock4> result = queryListJob.result();

    // Assert
    System.out.println(result);
    for (int i = 0; i < result.size(); i++) {
      Mock4 resultMock = result.get(i);
      assertThat(resultMock.getTitle()).isEqualTo("some_title");
      assertThat(resultMock.getName()).isEqualTo("some_name");
      assertThat(resultMock.getParentParam()).isEqualTo("some_parent_param");
      assertThat(resultMock.get_id()).isEqualTo(20L+i);
      assertThat(resultMock.getId()).isEqualTo(30L+i);
    }
  }

  @Test public void shouldQueryProperRow() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    Mock4 mock1 = new Mock4(
        20L,
        30L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    Mock4 mock2 = new Mock4(
        21L,
        31L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    Mock4 mock3 = new Mock4(
        22L,
        32L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob(Mock4.class, mock1));
    executor.execute(new InsertJob(Mock4.class, mock2));
    executor.execute(new InsertJob(Mock4.class, mock3));
    QueryJob<Mock4> queryJob = new QueryJob<Mock4>(Mock4.class, () -> "_id = 21");
    executor.execute(queryJob);

    // Assert
    Mock4 properResult = queryJob.result();
    assertThat(properResult.getTitle()).isEqualTo("some_title");
    assertThat(properResult.getName()).isEqualTo("some_name");
    assertThat(properResult.getParentParam()).isEqualTo("some_parent_param");
    assertThat(properResult.get_id()).isEqualTo(21L);
    assertThat(properResult.getId()).isEqualTo(31L);

  }

  @Test public void shouldUpdateMock4() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    Mock4 mock1 = new Mock4(
        20L,
        30L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    Mock4 mock2 = new Mock4(
        21L,
        31L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    Mock4 mock3 = new Mock4(
        22L,
        32L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob(Mock4.class, mock1));
    executor.execute(new InsertJob(Mock4.class, mock2));
    executor.execute(new InsertJob(Mock4.class, mock3));

    QueryJob<Mock4> queryJob = new QueryJob<Mock4>(Mock4.class, () -> "_id = 20");

    // Assert
    executor.execute(queryJob);
    Mock4 noUpdateResult = queryJob.result();
    assertThat(noUpdateResult.getTitle()).isEqualTo("some_title");
    assertThat(noUpdateResult.getName()).isEqualTo("some_name");
    assertThat(noUpdateResult.getParentParam()).isEqualTo("some_parent_param");
    assertThat(noUpdateResult.get_id()).isEqualTo(20L);
    assertThat(noUpdateResult.getId()).isEqualTo(30L);


    mock1.setName("updated_name");
    mock1.setTitle("updated_title");
    executor.execute(new UpdateJob(Mock4.class, mock1));

    executor.execute(queryJob);
    Mock4 updateResult = queryJob.result();
    assertThat(updateResult.getTitle()).isEqualTo("updated_title");
    assertThat(updateResult.getName()).isEqualTo("updated_name");
    assertThat(updateResult.getParentParam()).isEqualTo("some_parent_param");
    assertThat(updateResult.get_id()).isEqualTo(20L);
    assertThat(updateResult.getId()).isEqualTo(30L);
  }

}