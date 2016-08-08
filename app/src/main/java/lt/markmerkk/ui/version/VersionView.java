package lt.markmerkk.ui.version;

import com.airhacks.afterburner.views.FXMLView;
import lt.markmerkk.ui.interfaces.DialogListener;

/**
 * Created by mariusmerkevicius on 12/14/15.
 * Represents the view to update the log
 */
public class VersionView extends FXMLView {
  public VersionView(DialogListener dialogListener) {
    ((VersionPresenter)getPresenter()).dialogListener = dialogListener;
  }
}
