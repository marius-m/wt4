package lt.markmerkk;

import java.util.List;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.database.DBMockExecutor;
import lt.markmerkk.storage2.SimpleLog;
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
public class DBTestExecutorProdLogTest {
  @Test public void shouldCreateTable() throws Exception {
    // Arrange
    DBMockExecutor
        executor = new lt.markmerkk.storage2.database.DBMockExecutor();
    // Act
    // Assert
    executor.execute(new CreateJob<>(SimpleLog.class));
  }

  @Test public void shouldInsert() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleLog log = new SimpleLogBuilder()
        .setStart(1000)
        .setEnd(2000)
        .setTask("TT-182")
        .setComment("Some comment")
        .build();

    // Act
    // Assert
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    executor.execute(new InsertJob(SimpleLog.class, log));
  }

  @Test public void shouldInsertMany() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();

    // Act
    // Assert
    SimpleLogBuilder builder = new SimpleLogBuilder()
        .setStart(1000)
        .setEnd(2000)
        .setTask("TT-182")
        .setComment("Some comment");
    SimpleLog log = builder.build();
    SimpleLog log2 = builder.build();
    SimpleLog log3 = builder.build();
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    executor.execute(new InsertJob(SimpleLog.class, log));
    executor.execute(new InsertJob(SimpleLog.class, log2));
    executor.execute(new InsertJob(SimpleLog.class, log3));

    assertThat(log.get_id()).isEqualTo(1);
    assertThat(log2.get_id()).isEqualTo(2);
    assertThat(log3.get_id()).isEqualTo(3);
  }

  @Test public void shouldInsertQuery() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleLogBuilder builder = new SimpleLogBuilder()
        .setStart(1000)
        .setEnd(2000)
        .setTask("TT-182")
        .setComment("Some comment");
    SimpleLog log = builder.build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    executor.execute(new InsertJob(SimpleLog.class, log));

    QueryJob<SimpleLog> queryJob = new QueryJob<SimpleLog>(SimpleLog.class, () -> "_id = 1");
    executor.execute(queryJob);
    SimpleLog result = queryJob.result();

    // Assert
    System.out.println(result);
    assertThat(result.getStart()).isEqualTo(1000);
    assertThat(result.getEnd()).isEqualTo(2000);
    assertThat(result.getTask()).isEqualTo("TT-182");
    assertThat(result.getComment()).isEqualTo("Some comment");
    assertThat(result.isDeleted()).isFalse();
    assertThat(result.isDirty()).isTrue();
    assertThat(result.isError()).isFalse();
  }

  @Test public void shouldInsertWithRemoteDate() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleLogBuilder builder = new SimpleLogBuilder()
        .setStart(1000)
        .setEnd(2000)
        .setTask("TT-182")
        .setComment("Some comment");
    SimpleLog log = builder.build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    executor.execute(new InsertJob(SimpleLog.class, log));

    QueryJob<SimpleLog> queryJob = new QueryJob<SimpleLog>(SimpleLog.class, () -> "_id = 1");
    executor.execute(queryJob);
    SimpleLog result = queryJob.result();

    // Assert
    System.out.println(result);
    assertThat(result.getStart()).isEqualTo(1000);
    assertThat(result.getEnd()).isEqualTo(2000);
    assertThat(result.getTask()).isEqualTo("TT-182");
    assertThat(result.getComment()).isEqualTo("Some comment");
    assertThat(result.isDeleted()).isFalse();
    assertThat(result.isDirty()).isTrue();
    assertThat(result.isError()).isFalse();
  }

  @Test public void shouldQueryNonExisting() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleLogBuilder builder = new SimpleLogBuilder()
        .setStart(1000)
        .setEnd(2000)
        .setTask("TT-182")
        .setComment("Some comment");
    SimpleLog log = builder.build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));

    QueryJob<SimpleLog> queryJob = new QueryJob<SimpleLog>(SimpleLog.class, () -> "_id = 1");
    executor.execute(queryJob);
    assertThat(queryJob.result()).isNull();
  }

  @Test public void shouldQueryList() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleLogBuilder builder = new SimpleLogBuilder()
        .setTask("TT-182");
    SimpleLog log1 = builder.setStart(1000).setEnd(2000).setComment("Some comment1").build();
    SimpleLog log2 = builder.setStart(2000).setEnd(3000).setComment("Some comment2").build();
    SimpleLog log3 = builder.setStart(3000).setEnd(4000).setComment("Some comment3").build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    executor.execute(new InsertJob(SimpleLog.class, log1));
    executor.execute(new InsertJob(SimpleLog.class, log2));
    executor.execute(new InsertJob(SimpleLog.class, log3));
    QueryListJob<SimpleLog> queryListJob = new QueryListJob<SimpleLog>(SimpleLog.class);
    executor.execute(queryListJob);
    List<SimpleLog> result = queryListJob.result();

    // Assert
    System.out.println(result);
    assertThat(result.size()).isEqualTo(3);
    for (int i = 0; i < result.size(); i++) {
      SimpleLog resultLog = result.get(i);
      assertThat(resultLog.getComment()).isEqualTo("Some comment" + (i + 1));
    }
  }

  @Test public void shouldQueryListDelete() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleLogBuilder builder = new SimpleLogBuilder()
        .setTask("TT-182");
    SimpleLog log1 = builder.setStart(1000).setEnd(2000).setComment("Some comment1").build();
    SimpleLog log2 = builder.setStart(2000).setEnd(3000).setComment("Some comment2").build();
    SimpleLog log3 = builder.setStart(3000).setEnd(4000).setComment("Some comment3").build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    executor.execute(new InsertJob(SimpleLog.class, log1));
    executor.execute(new InsertJob(SimpleLog.class, log2));
    executor.execute(new InsertJob(SimpleLog.class, log3));
    QueryListJob<SimpleLog> queryListJob = new QueryListJob<SimpleLog>(SimpleLog.class);
    executor.execute(queryListJob);
    List<SimpleLog> result = queryListJob.result();

    // Assert
    assertThat(result.size()).isEqualTo(3);
    for (int i = 0; i < result.size(); i++) {
      SimpleLog resultLog = result.get(i);
      assertThat(resultLog.getComment()).isEqualTo("Some comment" + (i + 1));
    }

    executor.execute(new DeleteJob(SimpleLog.class, log1));
    queryListJob = new QueryListJob<SimpleLog>(SimpleLog.class);
    executor.execute(queryListJob);
    result = queryListJob.result();
    assertThat(result.size()).isEqualTo(2);
  }


  @Test public void shouldQuerySpecific() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleLogBuilder builder = new SimpleLogBuilder()
        .setTask("TT-182");
    SimpleLog log1 = builder.setStart(1000).setEnd(2000).setComment("Some comment1").build();
    SimpleLog log2 = builder.setStart(2000).setEnd(3000).setComment("Some comment2").build();
    SimpleLog log3 = builder.setStart(3000).setEnd(4000).setComment("Some comment3").build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    executor.execute(new InsertJob(SimpleLog.class, log1));
    executor.execute(new InsertJob(SimpleLog.class, log2));
    executor.execute(new InsertJob(SimpleLog.class, log3));


    // Assert
    QueryJob<SimpleLog> queryJob = new QueryJob<SimpleLog>(SimpleLog.class, () -> "_id = 2");
    executor.execute(queryJob);
    SimpleLog result = queryJob.result();

    // Assert
    assertThat(result.getStart()).isEqualTo(2000);
    assertThat(result.getEnd()).isEqualTo(3000);
    assertThat(result.getTask()).isEqualTo("TT-182");
    assertThat(result.getComment()).isEqualTo("Some comment2");

  }

  @Test public void shouldUpdate() throws Exception {
    // Arrange
    DBMockExecutor executor = new DBMockExecutor();
    SimpleLogBuilder builder = new SimpleLogBuilder()
        .setTask("TT-182");
    SimpleLog log1 = builder.setStart(1000).setEnd(2000).setComment("Some comment1").build();
    SimpleLog log2 = builder.setStart(2000).setEnd(3000).setComment("Some comment2").build();
    SimpleLog log3 = builder.setStart(3000).setEnd(4000).setComment("Some comment3").build();

    // Act
    executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    executor.execute(new InsertJob(SimpleLog.class, log1));
    executor.execute(new InsertJob(SimpleLog.class, log2));
    executor.execute(new InsertJob(SimpleLog.class, log3));


    // Assert
    QueryJob<SimpleLog> queryJob = new QueryJob<SimpleLog>(SimpleLog.class, () -> "_id = 2");
    executor.execute(queryJob);
    SimpleLog result = queryJob.result();

    // Assert
    assertThat(result.getStart()).isEqualTo(2000);
    assertThat(result.getEnd()).isEqualTo(3000);
    assertThat(result.getTask()).isEqualTo("TT-182");
    assertThat(result.getComment()).isEqualTo("Some comment2");

    result = new SimpleLogBuilder(result)
        .setTask("TT-666")
        .setComment("No comment")
        .build();
    executor.execute(new UpdateJob(SimpleLog.class, result));

    executor.execute(queryJob); // requerying
    SimpleLog updateResult = queryJob.result();
    assertThat(updateResult.getStart()).isEqualTo(2000);
    assertThat(updateResult.getEnd()).isEqualTo(3000);
    assertThat(updateResult.getTask()).isEqualTo("TT-666");
    assertThat(updateResult.getComment()).isEqualTo("No comment");
  }


}