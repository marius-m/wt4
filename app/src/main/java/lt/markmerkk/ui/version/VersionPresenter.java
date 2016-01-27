package lt.markmerkk.ui.version;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lt.markmerkk.ui.interfaces.DialogListener;

/**
 * Created by mariusmerkevicius on 12/14/15. Represents the presenter to update the log
 */
public class VersionPresenter {

  @FXML
  Button buttonClose;

  DialogListener dialogListener;

  public void onClickClose() {
    if (dialogListener == null) return;
    dialogListener.onCancel();
  }

  public void onClickSave() {
  }

  //region Listeners

  public void setDialogListener(DialogListener dialogListener) {
    this.dialogListener = dialogListener;
  }

  //endregion

}
