package lt.markmerkk.ui.interfaces;

/**
 * Helper listener for the log update
 */
public interface DialogListener {
  /**
   * Called whenever items is saved and needs refreshing
   */
  void onSave();

  /**
   * Called whenever item update is cancelled
   */
  void onCancel();
}
