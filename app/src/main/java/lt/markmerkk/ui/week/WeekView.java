package lt.markmerkk.ui.week;

import com.airhacks.afterburner.views.FXMLView;
import lt.markmerkk.ui.display.DisplayLogPresenter;
import lt.markmerkk.ui.interfaces.UpdateListener;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the view to display the log in a week scale
 */
public class WeekView extends FXMLView {
  public WeekView(UpdateListener listener) {
    ((WeekPresenter) getPresenter()).setUpdateListener(listener);
  }
}
