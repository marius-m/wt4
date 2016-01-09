package lt.markmerkk.ui.taskweb;

import com.sun.webkit.network.CookieManager;
import java.net.CookieHandler;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the presenter for the webview to open new url
 */
public class TaskWebPresenter {

  @FXML WebView webView;

  public void open(String url) {
//    CookieManager handler = (CookieManager) CookieHandler.getDefault();
    webView.getEngine().load(url);
  }
}
