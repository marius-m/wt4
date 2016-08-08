package lt.markmerkk.ui.update;

import com.airhacks.afterburner.views.FXMLView;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.ui.interfaces.DialogListener;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the view to update the log
 */
public class UpdateLogView extends FXMLView {
  public UpdateLogView(DialogListener dialogListener, SimpleLog simpleLog) {
    ((UpdateLogPresenter)getPresenter()).dialogListener = dialogListener;
    ((UpdateLogPresenter)getPresenter()).initWithEntity(simpleLog);
  }
}
