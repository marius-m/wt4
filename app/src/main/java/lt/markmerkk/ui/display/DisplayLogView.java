package lt.markmerkk.ui.display;

import com.airhacks.afterburner.views.FXMLView;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the view to display the log list
 */
public class DisplayLogView extends FXMLView {
  public DisplayLogView(DisplayLogPresenter.Listener listener) {
    ((DisplayLogPresenter) getPresenter()).setListener(listener);
  }
}