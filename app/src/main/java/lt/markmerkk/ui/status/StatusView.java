package lt.markmerkk.ui.status;

import com.airhacks.afterburner.views.FXMLView;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.update.UpdateLogPresenter;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the view to show app status
 */
public class StatusView extends FXMLView {
  public StatusView(StatusPresenter.Listener listener) {
    ((StatusPresenter)getPresenter()).setListener(listener);
  }

}
