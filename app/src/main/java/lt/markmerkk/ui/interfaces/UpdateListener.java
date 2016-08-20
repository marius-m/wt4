package lt.markmerkk.ui.interfaces;

import lt.markmerkk.entities.SimpleLog;

/**
 * Helper listener for the display
 */
public interface UpdateListener {
  /**
   * Called whenever items is being updated
   * @param object item set for update
   */
  void onUpdate(SimpleLog object);

  /**
   * Called whenever items is deleted
   * @param object item set for delete
   */
  void onDelete(SimpleLog object);

  /**
   * Called whenever items is being cloned
   * @param object item set for clone
   */
  void onClone(SimpleLog object);
}
