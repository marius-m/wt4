package lt.markmerkk.ui.taskweb;

import com.airhacks.afterburner.views.FXMLView;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the view for the webview to open new url
 */
public class TaskWebView extends FXMLView {
  public TaskWebView(String url) {
    ((TaskWebPresenter)getPresenter()).open(url);
  }
}
