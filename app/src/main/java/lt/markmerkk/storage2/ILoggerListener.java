package lt.markmerkk.storage2;

import javafx.collections.ObservableList;

/**
 * A callback listener when some event occurs on
 * storage
 */
public interface ILoggerListener<T> {
  /**
   * Called when new data is available
   * @param data
   */
  void onDataChange(ObservableList<T> data);

}
