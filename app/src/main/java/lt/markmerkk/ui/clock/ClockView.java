package lt.markmerkk.ui.clock;

import com.airhacks.afterburner.views.FXMLView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the view with the clock for logging info.
 */
public class ClockView extends FXMLView {
  public ClockView(ClockPresenter.Listener listener) {
    ((ClockPresenter) getPresenter()).listener = listener;
  }
}