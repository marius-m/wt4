package lt.markmerkk.ui.version;

import com.vinumeris.updatefx.UpdateSummary;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javax.inject.Inject;
import lt.markmerkk.Main;
import lt.markmerkk.Translation;
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

  @Inject VersionController versionController;

  @FXML Button buttonClose;
  @FXML Hyperlink buttonTitle;
  @FXML Hyperlink buttonAuthor;
  @FXML Hyperlink buttonPlace;
  @FXML Hyperlink buttonUpdate;
  @FXML Text labelVersion;
  @FXML ProgressIndicator progressIndicator;

  DialogListener dialogListener;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    versionController.addListener(this);
    labelVersion.setText(String.format("Version: %s", Main.VERSION_NAME));
    onProgressChange(versionController.getProgress());
    onSummaryUpdate(versionController.getSummary());
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

  public void onClickSave() { }

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
    if (versionController.getSummary() != null
        && versionController.getSummary().highestVersion > Main.VERSION_CODE) {
      versionController.upgrade();
      return;
    }
    versionController.checkForUpdate();
  }

  @Override
  public void onProgressChange(double progressChange) {
    progressIndicator.setProgress((progressChange <= 0) ? 0 : progressChange);
  }

  @Override
  public void onSummaryUpdate(UpdateSummary updateSummary) {
    if (versionController.getProgress() > 0 && versionController.getProgress() < 1) {
      buttonUpdate.setText(Translation.getInstance().getString("about_status_updating"));
      return;
    }
    if (updateSummary == null) {
      buttonUpdate.setText(Translation.getInstance().getString("about_status_no_info"));
      return;
    }
    if (updateSummary.highestVersion > Main.VERSION_CODE) {
      buttonUpdate.setText(Translation.getInstance().getString("about_status_update_available"));
      return;
    }
    buttonUpdate.setText(Translation.getInstance().getString("about_status_update_unavailable"));
  }

  //region Listeners

  public void setDialogListener(DialogListener dialogListener) {
    this.dialogListener = dialogListener;
  }

  //endregion

}
