package lt.markmerkk.ui.version;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javax.inject.Inject;
import lt.markmerkk.Main;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.ui.interfaces.DialogListener;
import lt.markmerkk.utils.VersionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 12/14/15. Represents the presenter to update the log
 */
public class VersionPresenter implements Initializable, Destroyable, VersionController.UpgradeListener {
  Logger logger = LoggerFactory.getLogger(VersionPresenter.class);

  @Inject
  VersionController versionController;

  @FXML
  Button buttonClose;
  @FXML
  Hyperlink buttonTitle;
  @FXML
  Hyperlink buttonAuthor;
  @FXML
  Hyperlink buttonPlace;
  @FXML
  Hyperlink buttonUpdate;
  @FXML
  ProgressIndicator progressIndicator;

  DialogListener dialogListener;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    versionController.addListener(this);
//    onProgressChange(versionController.getProgress());
//    onSummaryUpdate(versionController.getSummary());
  }

  @Override
  public void destroy() {
    versionController.removeListener(this);
  }

  public void onClickClose() {
    destroy();
    if (dialogListener == null) return;
    dialogListener.onCancel();
  }

  public void onClickSave() {
  }

  public void onClickTitle() {
    if (Main.hostServices != null)
      Main.hostServices.showDocument("https://bitbucket.org/mmerkevicius/wt4");
  }

  public void onClickAuthor() {
    if (Main.hostServices != null)
      Main.hostServices.showDocument("https://github.com/marius-m");
  }

  public void onClickPlace() {
    if (Main.hostServices != null)
      Main.hostServices.showDocument("http://ito.lt");
  }

  public void onClickUpdate() {
//    if (versionController.getSummary() != null
//        && versionController.getSummary().highestVersion > Main.VERSION) {
//      versionController.upgrade();
//      return;
//    }
//    versionController.checkForUpdate();
  }

  @Override
  public void onProgressChange(double progressChange) {
    progressIndicator.setProgress((progressChange <= 0) ? 0 : progressChange);
  }

//  @Override
//  public void onSummaryUpdate(UpdateSummary updateSummary) {
//    if (versionController.getProgress() > 0 && versionController.getProgress() < 1) {
//      buttonUpdate.setText("Updating...");
//      return;
//    }
//    if (updateSummary == null) {
//      buttonUpdate.setText("No information about the update!");
//      return;
//    }
//    if (updateSummary.highestVersion > Main.VERSION) {
//      buttonUpdate.setText("New updates are available! Update? (Will restart the app).");
//      return;
//    }
//    buttonUpdate.setText("App is up to date!");
//  }

  //region Listeners

  public void setDialogListener(DialogListener dialogListener) {
    this.dialogListener = dialogListener;
  }

  //endregion

}
