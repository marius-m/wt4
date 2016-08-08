package lt.markmerkk.entities;

import javafx.collections.ObservableList;

/**
 * A callback listener when some event occurs on
 * storage
 */
public interface IDataListener<T> {
  /**
   * Called when new data is available
   * @param data
   */
  void onDataChange(ObservableList<T> data);

}
