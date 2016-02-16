package lt.markmerkk.utils.abs;

import javafx.event.Event;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * Created by mariusmerkevicius on 2/3/16.
 * Provides additional functionality to the combo box
 * to be executable with text input
 */
public abstract class SearchableComboBoxDecorator<T> {
  protected ComboBox<T> comboBox;
  protected ProgressIndicator loadProgressIndicator;

  public SearchableComboBoxDecorator(ComboBox<T> comboBox, ProgressIndicator progressIndicator) {
    if (comboBox == null)
      throw new IllegalArgumentException("comboBox == null");
    if (progressIndicator == null)
      throw new IllegalArgumentException("progressIndicator == null");
    this.comboBox = comboBox;
    this.loadProgressIndicator = progressIndicator;
    init(comboBox);
  }

  public void init(ComboBox<T> comboBox) {
    //comboBox.setConverter(converter());
    comboBox.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
      onKeyEvent(event);
    });
  }

  //region Abstract

  protected abstract void onKeyEvent(KeyEvent keyEvent);

//  /**
//   * Display converter
//   * @return
//   */
//  protected abstract StringConverter<T> converter();

  //endregion

}
