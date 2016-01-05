package lt.markmerkk.jira;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * An abstract executor class to do various jobs in the background and control them
 */
public abstract class TaskExecutor<ResultType>  {
  private ScheduledExecutorService resultCheckExecutor;
  private ScheduledExecutorService readyCheckExecutor;
  private ExecutorService mainExecutor;
  private Future<ResultType> futureResult;

  public static final boolean DEBUG = false;
  private boolean loading = false;
  private ScheduledFuture futureReady;

  public TaskExecutor() { }

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
    mainExecutor = Executors.newSingleThreadExecutor();
    readyCheckExecutor = Executors.newScheduledThreadPool(1);
    resultCheckExecutor = Executors.newScheduledThreadPool(1);
    resultCheckExecutor.scheduleAtFixedRate(resultCheck, 0, 1, TimeUnit.SECONDS);

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
    resultCheckExecutor.shutdownNow();
    readyCheckExecutor.shutdownNow();
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
    futureResult = mainExecutor.submit(callable);
    setLoading(isLoading());
  }

  /**
   * Calls a cancel on currently loading task
   */
  public void cancel() {
    if (futureResult != null) futureResult.cancel(true);
    if (futureReady != null) futureReady.cancel(true);
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

  //region Runnables

  Runnable resultCheck = new Runnable() {
    @Override public void run() {
      printDebug("Check " + isLoading());
      if (TaskExecutor.this.loading != isLoading()) {
        if (!isLoading() && futureResult.isCancelled())
          Platform.runLater(TaskExecutor.this::onCancel);
        if (!isLoading() && !futureResult.isCancelled()) {
          try {
            printDebug("Result");
            ResultType result = futureResult.get(1, TimeUnit.SECONDS);
            Platform.runLater(() -> {
              futureResult = null;
              onResult(result);
            });

            futureReady = readyCheckExecutor.schedule(() -> {
              futureReady = null;
              onReady();
                }, 1, TimeUnit.SECONDS);


          } catch (InterruptedException e) {
            printDebug("Interruped getting");
            e.printStackTrace();
          } catch (ExecutionException e) {
            printDebug("Execution error");
            e.printStackTrace();
          } catch (TimeoutException e) {
            printDebug("Timeout exception");
            e.printStackTrace();
          }
        }
        futureResult = null;
      }
      setLoading(isLoading());
    }
  };

  //endregion
}
