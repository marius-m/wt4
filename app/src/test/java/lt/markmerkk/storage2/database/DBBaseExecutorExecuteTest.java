package lt.markmerkk.storage2.database;

import java.sql.Connection;
import java.sql.SQLException;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class DBBaseExecutorExecuteTest {
  @Test public void testNullQuery() throws Exception {
    // Arrange
    DBBaseExecutor executor = spy(new DBBaseExecutor());
    doReturn(mock(Connection.class)).when(executor).open(anyString());
    doNothing().when(executor).close(any(Connection.class));
    // Act
    executor.execute(null);

    // Assert
    verify(executor, never()).executeQuery(any(IQueryJob.class), any(Connection.class));
  }

  @Test public void testNullConnection() throws Exception {
    // Arrange
    DBBaseExecutor executor = spy(new DBBaseExecutor());
    doReturn(null).when(executor).open(anyString());
    doNothing().when(executor).close(any(Connection.class));
    // Act
    executor.execute(mock(IQueryJob.class));

    // Assert
    verify(executor, never()).executeQuery(any(IQueryJob.class), any(Connection.class));
  }

  @Test public void testThrowOnConnection1() throws Exception {
    // Arrange
    DBBaseExecutor executor = spy(new DBBaseExecutor());
    doThrow(new ClassNotFoundException()).when(executor).open(anyString());
    doNothing().when(executor).close(any(Connection.class));
    // Act
    executor.execute(mock(IQueryJob.class));

    // Assert
    verify(executor, never()).executeQuery(any(IQueryJob.class), any(Connection.class));
  }

  @Test public void testThrowOnConnection2() throws Exception {
    // Arrange
    DBBaseExecutor executor = spy(new DBBaseExecutor());
    doThrow(new SQLException()).when(executor).open(anyString());
    doNothing().when(executor).close(any(Connection.class));
    // Act
    executor.execute(mock(IQueryJob.class));

    // Assert
    verify(executor, never()).executeQuery(any(IQueryJob.class), any(Connection.class));
  }

  @Test public void testValid() throws Exception {
    // Arrange
    DBBaseExecutor executor = spy(new DBBaseExecutor());
    doReturn(mock(Connection.class)).when(executor).open(anyString());
    doNothing().when(executor).close(any(Connection.class));
    // Act
    executor.execute(mock(IQueryJob.class));

    // Assert
    verify(executor).executeQuery(any(IQueryJob.class), any(Connection.class));
  }
}