package lt.markmerkk.ui.display

import com.airhacks.afterburner.views.FXMLView
import lt.markmerkk.ui.interfaces.UpdateListener

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the view to display the log list
 */
class DisplayLogView(
        listener: UpdateListener,
        isViewSimplified: Boolean
) : FXMLView() {
    init {
        (presenter as DisplayLogPresenter).setUpdateListener(listener)
        (presenter as DisplayLogPresenter).initTableView(isViewSimplified)
    }
}
