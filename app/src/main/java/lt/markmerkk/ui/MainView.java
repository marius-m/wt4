package lt.markmerkk.ui;

import com.airhacks.afterburner.views.FXMLView;
import javafx.stage.Stage;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the main view of the app
 */
public class MainView extends FXMLView {
  public MainView(Stage stage) {
    ((MainPresenter) getPresenter()).setStage(stage);
  }
}
