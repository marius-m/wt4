package lt.markmerkk.ui.interfaces;

import lt.markmerkk.storage2.SimpleLog;

/**
 * Helper listener for the display
 */
public interface UpdateListener {
  /**
   * Called whenever items is being updated
   * @param object item set for update
   */
  void onUpdate(SimpleLog object);
}
