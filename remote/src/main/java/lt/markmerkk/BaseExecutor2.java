package lt.markmerkk;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Base class of a background executor to execute methods.
 *
 * Only one execution can be done at time.
 */
public abstract class BaseExecutor2 {
  private static final Logger logger = LoggerFactory.getLogger(BaseExecutor2.class);
  private ListenableFuture future;
  private ListeningExecutorService mainExecutor;
  //private Handler handler = new Handler();

  public static final boolean DEBUG = false;
  private boolean loading = false;

  public BaseExecutor2() {
  }

  //region Abstract

  /**
   * Called whenever cancel is executed
   */
  protected abstract void onCancel();

  /**
   * Called whenever executor is ready for another task
   */
  protected abstract void onReady();

  /**
   * Executed when background task is finished
   */
  protected abstract void onFinish();

  /**
   * Executed when there are changes in the loading state.
   */
  protected abstract void onLoadChange(boolean loading);

  //endregion

  public void onStart() {
    mainExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
  }

  public void onStop() {
    if (mainExecutor == null) return;
    try {
      mainExecutor.shutdown();
      mainExecutor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
    } finally {
      if (!mainExecutor.isTerminated())
        mainExecutor.shutdownNow();
    }
  }

  //region Convenience

  /**
   * An execution method
   */
  public void executeInBackground(Runnable runnable) {
    printDebug("Queue for execution");
    if (runnable == null) return;
    if (isLoading()) return;
    printDebug("Executing");
    setLoading(true);
    future = mainExecutor.submit(runnable);
    Futures.addCallback(future, new FutureCallback<Void>() {
      @Override
      public void onSuccess(Void resultType) {
        setLoading(false);
        onFinish();
        onReady();
      }

      @Override
      public void onFailure(Throwable throwable) {
        setLoading(false);
        onFinish();
        onReady();
      }
    });
  }

  /**
   * Calls a cancel on currently loading task
   */
  public void cancel() {
    if (future == null) return;
    onCancel();
    future.cancel(true);
    printDebug("Cancel");
    setLoading(false);
  }

  /**
   * Convenience method to print out logic for debugging workflow
   */
  private void printDebug(String message) {
    if (!DEBUG) return;
    logger.info(message);
  }

  //endregion

  //region Getters / setters

  /**
   * Returns current state of loading
   */
  public boolean isLoading() {
    if (future == null) return false;
    if (future.isDone()) return false;
    if (future.isCancelled()) return false;
    return true;
  }

  void setLoading(boolean loading) {
    if (this.loading == loading)
      return;
    this.loading = loading;
    printDebug("Loading: " + loading);
    onLoadChange(loading);
  }
}

//endregion
