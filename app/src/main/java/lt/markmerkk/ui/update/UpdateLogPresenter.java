package lt.markmerkk.ui.update;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.inject.Inject;

import lt.markmerkk.Main;
import lt.markmerkk.Translation;
import lt.markmerkk.LogStorage;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.entities.SimpleLogBuilder;
import lt.markmerkk.ui.interfaces.DialogListener;
import lt.markmerkk.utils.LogFormatters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the presenter to update the log
 */
public class UpdateLogPresenter {
  public static final Logger logger = LoggerFactory.getLogger(UpdateLogPresenter.class);
  @Inject
  LogStorage storage;

  @FXML TextField taskInput;
  @FXML TextField startInput;
  @FXML TextField endInput;
  @FXML TextArea commentInput;
  @FXML TextField outputInfo;
  @FXML Button buttonOk;
  @FXML Button buttonClose;

  DialogListener dialogListener;
  SimpleLog entity;

  protected void initWithEntity(SimpleLog entity) {
    Main.Companion.getComponent().presenterComponent().inject(this);
    this.entity = entity;

    startInput.setText(entity.getLongStart());
    startInput.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
                          String newValue) {
        update();
      }
    });
    endInput.setText(entity.getLongEnd());
    endInput.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
                          String newValue) {
        update();
      }
    });
    commentInput.setText(entity.getComment());
    taskInput.setText(entity.getTask());
    outputInfo.focusedProperty().addListener((observable, oldValue, newValue) -> {
      buttonClose.requestFocus(); // Trick to disable selection on the header
    });

    updateStatus(entity);
    update();
    updateLock();
  }

  //region Convenience

  /**
   * Responsible for updating header text and status color
   */
  void updateStatus(SimpleLog entity) {
    if (entity == null) return;
    if (entity.isError()) {
      setStatusError(entity.getErrorMessage());
      return;
    }
    if (entity.getId() > 0) {
      setStatusInSync();
      return;
    }
    setStatusUnSync();
  }

  /**
   * Sets header status as an error with a message
   * @param error
   */
  void setStatusError(String error) {
    outputInfo.setText(String.format(Translation.getInstance().getString("update_status_error"), error));
    outputInfo.setStyle("-fx-background-color: #AC725E;");
  }

  /**
   * Sets status as a success in sync
   */
  void setStatusInSync() {
    outputInfo.setText(Translation.getInstance().getString("update_status_synced"));
    outputInfo.setStyle("-fx-background-color: #9FE1E7;");
  }

  /**
   * Sets status as log not synced yet
   */
  void setStatusUnSync() {
    outputInfo.setText(Translation.getInstance().getString("update_status_unsynced"));
    outputInfo.setStyle("-fx-background-color: #FBE983;");
  }

  /**
   * Updates locking mechanism. Temoprarily, tasks that have been uploaded cant be edited.
   */
  void updateLock() {
    if (entity.getId() == 0) return; // Its local
    taskInput.setEditable(false);
    startInput.setEditable(false);
    endInput.setEditable(false);
    commentInput.setEditable(false);
    buttonOk.setDisable(true);
  }

  /**
   * Recalculates and updates info according to input change
   */
  private void update() {
    try {
      entity = new SimpleLogBuilder(entity)
          .setStart(LogFormatters.INSTANCE.getLongFormat().parseDateTime(startInput.getText()).getMillis())
          .setEnd(LogFormatters.INSTANCE.getLongFormat().parseDateTime(endInput.getText()).getMillis())
          .setTask(taskInput.getText())
          .setComment(commentInput.getText())
          .build();
      updateSaveTitle(entity.getPrettyDuration());
      buttonOk.setDisable(false);
    } catch (IllegalArgumentException e) {
      updateSaveTitle("Error: " + e.getMessage());
      buttonOk.setDisable(true);
    }
  }

  /**
   * Convenience method to update save button title
   * @param message
   */
  void updateSaveTitle(String message) {
    if (message == null) {
      buttonOk.setText("");
      return;
    }
    buttonOk.setText(String.format("%s", message));
  }

  //endregion

  //region View events

  public void onClickClose() {
    if (dialogListener == null) return;
    dialogListener.onCancel();
  }

  public void onClickSave() {
    if (dialogListener == null) return;
    try {
      update();
      storage.update(entity);
      dialogListener.onSave();
    } catch (IllegalArgumentException e) { }
  }

  //endregion

  //region Listeners

  public void setDialogListener(DialogListener dialogListener) {
    this.dialogListener = dialogListener;
  }

  //endregion

}
