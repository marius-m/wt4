package lt.markmerkk;

import com.atlassian.core.servlet.AbstractNoOpServlet;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 1/6/16.
 */
// Integration tests!
  @Ignore
public class AutoSyncProdTest {

  private AutoSync autoSync;
  private AutoSync.Listener listener;

  @Before
  public void setUp() {
    listener = mock(AutoSync.Listener.class);
    autoSync = new AutoSync();
    autoSync.setListener(listener);
  }

  @After
  public void tearDown() {
    autoSync.stop();
  }

  @Test
  public void testValid() throws Exception {
    // Arrange
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        System.out.println("Tick!");
        return null;
      }
    }).when(listener).onTrigger();

    // Act
    autoSync.schedule(1, TimeUnit.SECONDS);

    // Assert
    Thread.sleep(3100);
    verify(listener, times(3)).onTrigger();
  }

  @Test
  public void testStopAfterOne() throws Exception {
    // Arrange
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        System.out.println("Tick!");
        return null;
      }
    }).when(listener).onTrigger();

    // Act
    autoSync.schedule(1, TimeUnit.SECONDS);

    // Assert
    Thread.sleep(1100);
    autoSync.stop();
    Thread.sleep(3100);
    verify(listener, times(1)).onTrigger();
  }

  @Test
  public void testNeverScheduled() throws Exception {
    // Arrange
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        System.out.println("Tick!");
        return null;
      }
    }).when(listener).onTrigger();

    // Act
    //autoSync.schedule(1, TimeUnit.SECONDS);

    // Assert
    Thread.sleep(3100);
    verify(listener, never()).onTrigger();
  }

  @Test
  public void testScheduleZeroShouldStop() throws Exception {
    // Arrange
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        System.out.println("Tick!");
        return null;
      }
    }).when(listener).onTrigger();

    // Act
    autoSync.schedule(0, TimeUnit.SECONDS);

    // Assert
    Thread.sleep(3100);
    verify(listener, never()).onTrigger();
  }

}