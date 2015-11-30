package lt.markmerkk;

import java.util.List;
import lt.markmerkk.storage2.SimpleIssue;
import lt.markmerkk.storage2.SimpleIssueBuilder;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.database.DBMockExecutor;
import lt.markmerkk.storage2.jobs.CreateJob;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import lt.markmerkk.storage2.jobs.QueryListJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DBTestExecutorProdIssueTest {
  @Test public void shouldCreateTable() throws Exception {
    // Arrange
    DBMockExecutor
        executor = new DBMockExecutor();
    // Act
    // Assert
    executor.execute(new CreateJob<>(SimpleIssue.class));
  }

  @Test public void shouldInsertQuery() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleIssue issue = new SimpleIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description")
        .build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleIssue.class));
    executor.execute(new InsertJob(SimpleIssue.class, issue));

    QueryJob<SimpleIssue> queryJob = new QueryJob<SimpleIssue>(SimpleIssue.class, () -> "_id = 1");
    executor.execute(queryJob);
    SimpleIssue result = queryJob.result();

    // Assert
    System.out.println(result);
    assertThat(result.getProject()).isEqualTo("TT");
    assertThat(result.getKey()).isEqualTo("TT-12");
    assertThat(result.getDescription()).isEqualTo("Some valid description");
    assertThat(result.isDeleted()).isFalse();
    assertThat(result.isDirty()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.get_id()).isEqualTo(1);
  }

  @Test public void shouldQueryNonExisting() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleIssue issue = new SimpleIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description")
        .build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleIssue.class));

    QueryJob<SimpleIssue> queryJob = new QueryJob<SimpleIssue>(SimpleIssue.class, () -> "_id = 1");
    executor.execute(queryJob);
    assertThat(queryJob.result()).isNull();
  }

  @Test public void shouldQueryList() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleIssueBuilder builder = new SimpleIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description");
    SimpleIssue issue1 = builder.build();
    SimpleIssue issue2 = builder.build();
    SimpleIssue issue3 = builder.build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleIssue.class));
    executor.execute(new InsertJob(SimpleIssue.class, issue1));
    executor.execute(new InsertJob(SimpleIssue.class, issue2));
    executor.execute(new InsertJob(SimpleIssue.class, issue3));
    QueryListJob<SimpleIssue> queryListJob = new QueryListJob<SimpleIssue>(SimpleIssue.class);
    executor.execute(queryListJob);
    List<SimpleIssue> result = queryListJob.result();

    // Assert
    assertThat(result.size()).isEqualTo(3);
    for (int i = 1; i <= result.size(); i++) {
      SimpleIssue resultLog = result.get(i-1);
      assertThat(resultLog).isNotNull();
      assertThat(resultLog.get_id()).isEqualTo(i);
    }
  }

  @Test public void shouldQueryListDelete() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleIssueBuilder builder = new SimpleIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description");
    SimpleIssue issue1 = builder.build();
    SimpleIssue issue2 = builder.build();
    SimpleIssue issue3 = builder.build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleIssue.class));
    executor.execute(new InsertJob(SimpleIssue.class, issue1));
    executor.execute(new InsertJob(SimpleIssue.class, issue2));
    executor.execute(new InsertJob(SimpleIssue.class, issue3));
    QueryListJob<SimpleIssue> queryListJob = new QueryListJob<SimpleIssue>(SimpleIssue.class);
    executor.execute(queryListJob);
    List<SimpleIssue> result = queryListJob.result();

    // Assert
    assertThat(result.size()).isEqualTo(3);
    for (int i = 1; i <= result.size(); i++) {
      SimpleIssue resultLog = result.get(i-1);
      assertThat(resultLog).isNotNull();
      assertThat(resultLog.get_id()).isEqualTo(i);
    }

    executor.execute(new DeleteJob(SimpleIssue.class, issue1));
    queryListJob = new QueryListJob<SimpleIssue>(SimpleIssue.class);
    executor.execute(queryListJob);
    result = queryListJob.result();
    assertThat(result.size()).isEqualTo(2);
  }

  @Test public void shouldUpdate() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleIssueBuilder builder = new SimpleIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description");
    SimpleIssue issue1 = builder.build();
    SimpleIssue issue2 = builder.build();
    SimpleIssue issue3 = builder.build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleIssue.class));
    executor.execute(new InsertJob(SimpleIssue.class, issue1));
    executor.execute(new InsertJob(SimpleIssue.class, issue2));
    executor.execute(new InsertJob(SimpleIssue.class, issue3));


    // Assert
    QueryJob<SimpleIssue> queryJob = new QueryJob<SimpleIssue>(SimpleIssue.class, () -> "_id = 2");
    executor.execute(queryJob);
    SimpleIssue result = queryJob.result();

    // Assert
    assertThat(result.getProject()).isEqualTo("TT");
    assertThat(result.getKey()).isEqualTo("TT-12");
    assertThat(result.getDescription()).isEqualTo("Some valid description");

    result = new SimpleIssueBuilder(result)
        .setProject("MM")
        .setKey("MM-12")
        .build();
    executor.execute(new UpdateJob(SimpleIssue.class, result));

    executor.execute(queryJob); // requerying
    SimpleIssue updateResult = queryJob.result();
    assertThat(updateResult.getProject()).isEqualTo("MM");
    assertThat(updateResult.getKey()).isEqualTo("MM-12");
  }


}