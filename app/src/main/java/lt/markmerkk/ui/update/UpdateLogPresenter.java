package lt.markmerkk.ui.update;

import com.airhacks.afterburner.views.FXMLView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the presenter to update the log
 */
public class UpdateLogPresenter implements Initializable {

  @FXML TextField taskInput;
  @FXML TextField startInput;
  @FXML TextField endInput;
  @FXML TextArea commentInput;
  @FXML TextField durationInput;
  @FXML TextField outputError;
  @FXML Button buttonOk;
  @FXML Button buttonCancel;

  Listener listener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    System.out.println(resources);
  }

  public void onClickCancel() {
    if (listener == null) return;
    listener.onFinish();
  }

  public void onClickSave() {
    if (listener == null) return;
    listener.onFinish();
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  /**
   * Helper listener for the log update
   */
  public interface Listener {
    /**
     * Called whenever items is finished updating
     */
    void onFinish();
  }

}
