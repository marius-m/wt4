package lt.markmerkk.storage2.database;

import java.util.List;
import java.util.Set;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import lt.markmerkk.storage2.database.helpers.entities.Mock5;
import lt.markmerkk.storage2.jobs.CreateJob;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryDistinctListJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import lt.markmerkk.storage2.jobs.QueryListJob;
import lt.markmerkk.storage2.jobs.RowCountJob;
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

  @Test public void shouldQueryNonExisting() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    Mock3 mock3 = new Mock3("some_title", "some_param");
    executor.execute(new CreateJobIfNeeded<>(Mock3.class));
    QueryJob<Mock3> queryJob = new QueryJob<Mock3>(Mock3.class, () -> "title = 'some_title'");
    executor.execute(queryJob);
    assertThat(queryJob.result()).isNull();
  }

  @Test public void shouldInsertAndAssignMock5() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    Mock5 mock1 = new Mock5("some_title", "some_param");
    Mock5 mock2 = new Mock5("some_title2", "some_param2");
    executor.execute(new CreateJobIfNeeded<>(Mock5.class));
    executor.execute(new InsertJob(Mock5.class, mock1));
    executor.execute(new InsertJob(Mock5.class, mock2));

    assertThat(mock1.get_id()).isEqualTo(1);
    assertThat(mock2.get_id()).isEqualTo(2);
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
    assertThat(result.size()).isEqualTo(3);
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

  @Test public void shouldQueryListMock4WithClause() throws Exception {
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
    QueryListJob<Mock4> queryListJob = new QueryListJob<Mock4>(Mock4.class, () -> "_id = 21");
    executor.execute(queryListJob);
    List<Mock4> result = queryListJob.result();

    // Assert
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getTitle()).isEqualTo("some_title");
    assertThat(result.get(0).getName()).isEqualTo("some_name");
    assertThat(result.get(0).getParentParam()).isEqualTo("some_parent_param");
    assertThat(result.get(0).get_id()).isEqualTo(21L);
    assertThat(result.get(0).getId()).isEqualTo(31L);
  }

  @Test public void shouldQueryListDeleteMock4() throws Exception {
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
    assertThat(result.size()).isEqualTo(3);
    for (int i = 0; i < result.size(); i++) {
      Mock4 resultMock = result.get(i);
      assertThat(resultMock.getTitle()).isEqualTo("some_title");
      assertThat(resultMock.getName()).isEqualTo("some_name");
      assertThat(resultMock.getParentParam()).isEqualTo("some_parent_param");
      assertThat(resultMock.get_id()).isEqualTo(20L+i);
      assertThat(resultMock.getId()).isEqualTo(30L+i);
    }

    executor.execute(new DeleteJob(Mock4.class, result.get(2)));
    queryListJob = new QueryListJob<Mock4>(Mock4.class);
    executor.execute(queryListJob);
    result = queryListJob.result();
    assertThat(result.size()).isEqualTo(2);

    executor.execute(new DeleteJob(Mock4.class, result.get(0)));
    executor.execute(new DeleteJob(Mock4.class, result.get(1)));
    queryListJob = new QueryListJob<Mock4>(Mock4.class);
    executor.execute(queryListJob);
    result = queryListJob.result();
    assertThat(result.size()).isEqualTo(0);
  }

  @Test public void shouldQueryListOnEmptyMock4() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    QueryListJob<Mock4> queryListJob = new QueryListJob<Mock4>(Mock4.class);
    executor.execute(queryListJob);
    List<Mock4> result = queryListJob.result();

    // Assert
    assertThat(result.size()).isEqualTo(0);
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

  @Test public void shouldQueryDistinctListMock4() throws Exception {
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
        "different_title",
        "some_name"
    );
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob(Mock4.class, mock1));
    executor.execute(new InsertJob(Mock4.class, mock2));
    executor.execute(new InsertJob(Mock4.class, mock3));
    QueryDistinctListJob<Mock4> queryListJob = new QueryDistinctListJob<Mock4>(Mock4.class, () -> "title");
    executor.executeOrThrow(queryListJob);
    Set<String> result = queryListJob.result();

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(2);
  }

  @Test public void shouldQueryRowCountMock4() throws Exception {
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
        "different_title",
        "some_name"
    );
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob(Mock4.class, mock1));
    executor.execute(new InsertJob(Mock4.class, mock2));
    executor.execute(new InsertJob(Mock4.class, mock3));
    RowCountJob<Mock4> rowCountJob = new RowCountJob<Mock4>(Mock4.class);
    executor.executeOrThrow(rowCountJob);
    Integer result = rowCountJob.result();

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(3);
  }

  @Test public void shouldQueryRowCountMock4_wrongTable() throws Exception {
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
        "different_title",
        "some_name"
    );
    executor.execute(new CreateJobIfNeeded<>(Mock3.class));
    executor.execute(new CreateJobIfNeeded<>(Mock4.class));
    executor.execute(new InsertJob(Mock4.class, mock1));
    executor.execute(new InsertJob(Mock4.class, mock2));
    executor.execute(new InsertJob(Mock4.class, mock3));
    RowCountJob<Mock3> rowCountJob = new RowCountJob<Mock3>(Mock3.class); // Querying the wrong table
    executor.executeOrThrow(rowCountJob);
    Integer result = rowCountJob.result();

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(0);
  }


}