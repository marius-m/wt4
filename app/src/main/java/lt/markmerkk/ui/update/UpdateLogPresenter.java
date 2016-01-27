package lt.markmerkk.ui.update;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.inject.Inject;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.ui.interfaces.DialogListener;
import lt.markmerkk.utils.hourglass.HourGlass;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the presenter to update the log
 */
public class UpdateLogPresenter {
  @Inject BasicLogStorage storage;

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
    this.entity = entity;

    startInput.setText(entity.getLongStart());
    startInput.textProperty().addListener(new ChangeListener<String>() {
      @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        update();
      }
    });
    endInput.setText(entity.getLongEnd());
    endInput.textProperty().addListener(new ChangeListener<String>() {
      @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        update();
      }
    });
    commentInput.setText(entity.getComment());
    taskInput.setText(entity.getTask());
    outputInfo.setText((entity.isError()) ? entity.getErrorMessage() : "Updating: "+entity.toString());
    update();
    updateLock();
  }

  //region Convenience

  /**
   * Updates locking mechanism. Temoprarily, tasks that have been uploaded cant be edited.
   */
  void updateLock() {
    if (entity.getId() == 0) return; // Its local
    taskInput.setEditable(false);
    startInput.setEditable(false);
    endInput.setEditable(false);
    commentInput.setEditable(false);
    outputInfo.setEditable(false);
    buttonOk.setDisable(true);
    outputInfo.setText("Can't update logs that were uploaded!");
  }

  /**
   * Recalculates and updates info according to input change
   */
  private void update() {
    try {
      entity = new SimpleLogBuilder(entity)
          .setStart(HourGlass.longFormat.parseDateTime(startInput.getText()).getMillis())
          .setEnd(HourGlass.longFormat.parseDateTime(endInput.getText()).getMillis())
          .setTask(taskInput.getText())
          .setComment(commentInput.getText())
          .build();
      updateSaveTitle(entity.getPrettyDuration());
    } catch (IllegalArgumentException e) {
      updateSaveTitle("Error: " + e.getMessage());
    }
  }

  /**
   * Convenience method to update save button title
   * @param message
   */
  void updateSaveTitle(String message) {
    if (message == null) {
      buttonOk.setText("Save");
      return;
    }
    buttonOk.setText(String.format("Save (%s)", message));
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
