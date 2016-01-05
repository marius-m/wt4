package lt.markmerkk.jira;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * An abstract executor class to do various jobs in the background and control them
 */
public abstract class TaskExecutor2<ResultType>  {
  private ListenableFuture<ResultType> futureResult;
  private ListeningExecutorService mainExecutor;

  public static final boolean DEBUG = false;
  private boolean loading = false;
  //private ScheduledFuture futureReady;

  public TaskExecutor2() { }

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
   * @param result returned result
   */
  protected abstract void onResult(ResultType result);

  /**
   * Executed when there are changes in the loading state.
   * @param loading
   */
  protected abstract void onLoadChange(boolean loading);

  //endregion

  public void onStart() {
    mainExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
  }

  public void onStop() {
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
  protected void executeInBackground(Callable<ResultType> callable) {
    printDebug("Queue for execution");
    if (callable == null) return;
    if (isLoading()) return;
    printDebug("Executing");
    setLoading(true);
    futureResult = mainExecutor.submit(callable);
    Futures.addCallback(futureResult, new FutureCallback<ResultType>() {
      @Override public void onSuccess(ResultType resultType) {
        setLoading(false);
        Platform.runLater(() -> {
          onResult(resultType);
          onReady();
        });
      }

      @Override public void onFailure(Throwable throwable) {
        setLoading(false);
        System.out.println("Some error something");
      }
    });
  }

  /**
   * Calls a cancel on currently loading task
   */
  public void cancel() {
    if (futureResult != null) futureResult.cancel(true);
    printDebug("Cancel");
    onCancel();
    setLoading(false);
  }

  /**
   * Convenience method to print out logic for debugging workflow
   * @param message
   */
  private void printDebug(String message) {
    if (!DEBUG) return;
    System.out.println(message);
  }

  //endregion

  //region Getters / setters

  /**
   * Returns current state of loading
   * @return
   */
  public boolean isLoading() {
    if (futureResult == null) return false;
    if (futureResult.isDone()) return false;
    if (futureResult.isCancelled()) return false;
    return true;
  }

  void setLoading(boolean loading) {
    if (this.loading == loading)
      return;
    this.loading = loading;
    printDebug("Loading: " + loading);
    Platform.runLater(() -> onLoadChange(loading));
  }

  //endregion

}
