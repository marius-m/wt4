package lt.markmerkk.ui.version;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import lt.markmerkk.Main;
import lt.markmerkk.ui.interfaces.DialogListener;

/**
 * Created by mariusmerkevicius on 12/14/15. Represents the presenter to update the log
 */
public class VersionPresenter implements Initializable {

  @FXML Button buttonClose;
  @FXML Hyperlink buttonTitle;
  @FXML Hyperlink buttonAuthor;
  @FXML Hyperlink buttonPlace;

  DialogListener dialogListener;

  @Override
  public void initialize(URL location, ResourceBundle resources) { }

  public void onClickClose() {
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

  //region Listeners

  public void setDialogListener(DialogListener dialogListener) {
    this.dialogListener = dialogListener;
  }

  //endregion

}
