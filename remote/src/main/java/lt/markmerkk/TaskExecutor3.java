package lt.markmerkk.jira;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import lt.markmerkk.ui.status.StatusPresenter;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * An abstract executor class to do various jobs in the background and reports
 * its loging process
 */
public class TaskExecutor3  {
  private ListenableFuture futureResult;
  private ListeningExecutorService mainExecutor;

  public static final boolean DEBUG = false;
  private boolean loading = false;

  Listener listener;

  public TaskExecutor3() { }

  //region Abstract

  /**
   * Executed when there are changes in the loading state.
   * @param loading
   */
  void onLoadChange(boolean loading) {
    if (listener == null) return;
    listener.onLoadChange(loading);
  }

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
  public void executeInBackground(Runnable runnable) {
    printDebug("Queue for execution");
    if (runnable == null) return;
    if (isLoading()) return;
    printDebug("Executing");
    setLoading(true);
    futureResult = mainExecutor.submit(runnable);
    Futures.addCallback(futureResult, new FutureCallback<Void>() {
      @Override public void onSuccess(Void resultType) {
        setLoading(false);
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


  public void setListener(Listener listener) {
    this.listener = listener;
  }

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

  //region Classes

  public interface Listener {
    void onLoadChange(boolean loading);
  }

  //endregion

}
