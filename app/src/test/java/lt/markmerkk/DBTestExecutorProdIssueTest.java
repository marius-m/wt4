package lt.markmerkk;

import java.util.List;
import lt.markmerkk.entities.LocalIssue;
import lt.markmerkk.entities.LocalIssueBuilder;
import lt.markmerkk.entities.jobs.CreateJob;
import lt.markmerkk.entities.jobs.DeleteJob;
import lt.markmerkk.entities.jobs.InsertJob;
import lt.markmerkk.entities.jobs.QueryJob;
import lt.markmerkk.entities.jobs.QueryListJob;
import lt.markmerkk.entities.jobs.UpdateJob;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
@Ignore // Integration tests
public class DBTestExecutorProdIssueTest {
  @Test public void shouldCreateTable() throws Exception {
    // Arrange
    DBTestExecutor
        executor = new DBTestExecutor();
    // Act
    // Assert
    executor.execute(new CreateJob<>(LocalIssue.class));
  }

  @Test public void shouldInsertQuery() throws Exception {
    // Arrange
    DBTestExecutor executor = new DBTestExecutor();
    LocalIssue issue = new LocalIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description")
        .build();

    // Act
//    executor.execute(new CreateJobIfNeeded<>(LocalIssue.class));
    executor.execute(new InsertJob(LocalIssue.class, issue));

    QueryJob<LocalIssue> queryJob = new QueryJob<LocalIssue>(LocalIssue.class, () -> "_id = 1");
    executor.execute(queryJob);
    LocalIssue result = queryJob.result();

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
    DBTestExecutor executor = new DBTestExecutor();
    LocalIssue issue = new LocalIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description")
        .build();

    // Act
//    executor.execute(new CreateJobIfNeeded<>(LocalIssue.class));

    QueryJob<LocalIssue> queryJob = new QueryJob<LocalIssue>(LocalIssue.class, () -> "_id = 1");
    executor.execute(queryJob);
    assertThat(queryJob.result()).isNull();
  }

  @Test public void shouldQueryList() throws Exception {
    // Arrange
    DBTestExecutor executor = new DBTestExecutor();
    LocalIssueBuilder builder = new LocalIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description");
    LocalIssue issue1 = builder.build();
    LocalIssue issue2 = builder.build();
    LocalIssue issue3 = builder.build();

    // Act
//    executor.execute(new CreateJobIfNeeded<>(LocalIssue.class));
    executor.execute(new InsertJob(LocalIssue.class, issue1));
    executor.execute(new InsertJob(LocalIssue.class, issue2));
    executor.execute(new InsertJob(LocalIssue.class, issue3));
    QueryListJob<LocalIssue> queryListJob = new QueryListJob<LocalIssue>(LocalIssue.class);
    executor.execute(queryListJob);
    List<LocalIssue> result = queryListJob.result();

    // Assert
    assertThat(result.size()).isEqualTo(3);
    for (int i = 1; i <= result.size(); i++) {
      LocalIssue resultLog = result.get(i-1);
      assertThat(resultLog).isNotNull();
      assertThat(resultLog.get_id()).isEqualTo(i);
    }
  }

  @Test public void shouldQueryListDelete() throws Exception {
    // Arrange
    DBTestExecutor executor = new DBTestExecutor();
    LocalIssueBuilder builder = new LocalIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description");
    LocalIssue issue1 = builder.build();
    LocalIssue issue2 = builder.build();
    LocalIssue issue3 = builder.build();

    // Act
//    executor.execute(new CreateJobIfNeeded<>(LocalIssue.class));
    executor.execute(new InsertJob(LocalIssue.class, issue1));
    executor.execute(new InsertJob(LocalIssue.class, issue2));
    executor.execute(new InsertJob(LocalIssue.class, issue3));
    QueryListJob<LocalIssue> queryListJob = new QueryListJob<LocalIssue>(LocalIssue.class);
    executor.execute(queryListJob);
    List<LocalIssue> result = queryListJob.result();

    // Assert
    assertThat(result.size()).isEqualTo(3);
    for (int i = 1; i <= result.size(); i++) {
      LocalIssue resultLog = result.get(i-1);
      assertThat(resultLog).isNotNull();
      assertThat(resultLog.get_id()).isEqualTo(i);
    }

    executor.execute(new DeleteJob(LocalIssue.class, issue1));
    queryListJob = new QueryListJob<LocalIssue>(LocalIssue.class);
    executor.execute(queryListJob);
    result = queryListJob.result();
    assertThat(result.size()).isEqualTo(2);
  }

  @Test public void shouldUpdate() throws Exception {
    // Arrange
    DBTestExecutor executor = new DBTestExecutor();
    LocalIssueBuilder builder = new LocalIssueBuilder()
        .setProject("TT")
        .setKey("TT-12")
        .setDescription("Some valid description");
    LocalIssue issue1 = builder.build();
    LocalIssue issue2 = builder.build();
    LocalIssue issue3 = builder.build();

    // Act
//    executor.execute(new CreateJobIfNeeded<>(LocalIssue.class));
    executor.execute(new InsertJob(LocalIssue.class, issue1));
    executor.execute(new InsertJob(LocalIssue.class, issue2));
    executor.execute(new InsertJob(LocalIssue.class, issue3));


    // Assert
    QueryJob<LocalIssue> queryJob = new QueryJob<LocalIssue>(LocalIssue.class, () -> "_id = 2");
    executor.execute(queryJob);
    LocalIssue result = queryJob.result();

    // Assert
    assertThat(result.getProject()).isEqualTo("TT");
    assertThat(result.getKey()).isEqualTo("TT-12");
    assertThat(result.getDescription()).isEqualTo("Some valid description");

    result = new LocalIssueBuilder(result)
        .setProject("MM")
        .setKey("MM-12")
        .build();
    executor.execute(new UpdateJob(LocalIssue.class, result));

    executor.execute(queryJob); // requerying
    LocalIssue updateResult = queryJob.result();
    assertThat(updateResult.getProject()).isEqualTo("MM");
    assertThat(updateResult.getKey()).isEqualTo("MM-12");
  }


}