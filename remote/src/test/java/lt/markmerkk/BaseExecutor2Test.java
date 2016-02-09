package lt.markmerkk;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 1/23/16.
 */

// Integration tests!
@Ignore
public class BaseExecutor2Test {

  private BaseExecutor2 executor2;

  @Before
  public void setUp() throws Exception {
    executor2 = spy(new BaseExecutor2() {
      @Override
      protected void onCancel() {
        System.out.println("onCancel");
      }

      @Override
      protected void onReady() {
        System.out.println("onReady");
      }

      @Override
      protected void onFinish() {
        System.out.println("onFinish");
      }

      @Override
      protected void onLoadChange(boolean loading) {
        System.out.println("onLoadChange:" + loading);
      }

    });
    executor2.onStart();
  }

  @After
  public void tearDown() throws Exception {
    executor2.onStop();
  }

  @Test
  public void testValid() throws Exception {
    // Arrange

    // Act
    executor2.executeInBackground(tickerRunnable);

    // Assert
    Thread.sleep(7000);
    System.out.println("Full finish!");
    verify(executor2).onLoadChange(true);
    verify(executor2).onLoadChange(false);
    verify(executor2).onFinish();
    verify(executor2).onReady();
  }

  @Test
  public void testInterrupt() throws Exception {
    // Arrange

    // Act
    executor2.executeInBackground(tickerRunnable);

    // Assert
    Thread.sleep(3000);
    System.out.println("Full finish!");
    executor2.cancel();
    verify(executor2).onLoadChange(true);
    verify(executor2).onLoadChange(false);
    verify(executor2).onFinish();
    verify(executor2).onCancel();
    verify(executor2).onReady();

  }

  @Test public void testShortCircuit() throws Exception {
    // Arrange

    // Act
    executor2.executeInBackground(tickerRunnable);

    // Assert
    Thread.sleep(31000);
    System.out.println("Full finish!");
    executor2.cancel();
    verify(executor2).onLoadChange(true);
    verify(executor2).onLoadChange(false);
    verify(executor2).onFinish();
    verify(executor2).onCancel();
    verify(executor2).onReady();
  }

  //region Classes

  Runnable tickerRunnable = new Runnable() {
    @Override
    public void run() {
      int count = 0;
      try {
        do {
          Thread.sleep(1000);
          System.out.println("Tick tock " + count);
        } while (count++ < 5);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  };

  //endregion

}