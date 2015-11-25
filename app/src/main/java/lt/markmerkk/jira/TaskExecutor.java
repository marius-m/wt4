package lt.markmerkk.jira;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mariusmerkevicius on 11/25/15.
 */
public abstract class TaskExecutor<ResultType>  {
  private ScheduledExecutorService resultCheckExecutor = Executors.newScheduledThreadPool(1);
  private ExecutorService mainExecutor;
  private Future<ResultType> futureResult;

  private boolean loading = false;

  public TaskExecutor() { }

  //region Abstract

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
  }

  //region Convenience

  /**
   * An execution method
   */
  protected void executeInBackground(Callable<ResultType> callable) {
    if (callable == null) return;
    if (isLoading()) return;
    futureResult = mainExecutor.submit(callable);
    setLoading(isLoading());
  }

  /**
   * Calls a cancel on currently loading task
   */
  public void cancel() {
    if (futureResult == null) return;
    if (futureResult.isDone()) return;
    if (futureResult.isCancelled()) return;
    futureResult.cancel(true);
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
    onLoadChange(loading);
  }

  //endregion

  //region Runnables

  Runnable resultCheck = new Runnable() {
    @Override public void run() {
      if (TaskExecutor.this.loading != isLoading()) {
        if (!isLoading() && !futureResult.isCancelled()) {
          try {
            onResult(futureResult.get());
          } catch (InterruptedException e) {
            e.printStackTrace();
          } catch (ExecutionException e) {
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
