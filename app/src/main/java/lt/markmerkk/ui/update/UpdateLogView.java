package lt.markmerkk.ui.update;

import com.airhacks.afterburner.views.FXMLView;
import lt.markmerkk.storage2.SimpleLog;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the view to update the log
 */
public class UpdateLogView extends FXMLView {
  public UpdateLogView(UpdateLogPresenter.Listener listener, SimpleLog simpleLog) {
    ((UpdateLogPresenter)getPresenter()).listener = listener;
    ((UpdateLogPresenter)getPresenter()).initWithEntity(simpleLog);
  }
}
