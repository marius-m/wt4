package lt.markmerkk.ui.week;

import lt.markmerkk.BaseExecutor2;

/**
 * Created by mariusmerkevicius on 1/23/16.
 * Simple custom {@link Runnable} executor with {@link LoadListener}
 */
public class SimpleAsyncExecutor extends BaseExecutor2 {

  LoadListener listener;

  @Override
  protected void onCancel() { }

  @Override
  protected void onReady() { }

  @Override
  protected void onFinish() { }

  @Override
  protected void onLoadChange(boolean loading) {
    if (listener == null) return;
    listener.onLoadChange(loading);
  }

  public void setListener(LoadListener listener) {
    this.listener = listener;
  }

  /**
   * Loading state reporter
   */
  public interface LoadListener {
    /**
     * Reports of load change
     * @param loading
     */
    void onLoadChange(boolean loading);
  }


}
