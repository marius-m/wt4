package lt.markmerkk.ui.taskweb;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the presenter for the webview to open new url
 */
public class TaskWebPresenter {

  @FXML WebView webView;

  public void open(String url) {
    webView.getEngine().load(url);
  }
}
