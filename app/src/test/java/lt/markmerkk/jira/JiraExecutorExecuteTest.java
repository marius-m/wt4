package lt.markmerkk.jira;

import java.util.concurrent.Callable;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.JiraListener;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraExecutorExecuteTest {
  @Test public void testNull() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    doNothing().when(executor).executeInBackground(any(Callable.class));

    // Act
    executor.execute(null);

    // Assert
    verify(executor, never()).executeInBackground(any(Callable.class));
  }

  @Test public void testValid() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    doNothing().when(executor).executeInBackground(any(Callable.class));

    // Act
    executor.execute(mock(IWorker.class));

    // Assert
    verify(executor).executeInBackground(any(Callable.class));
  }

}