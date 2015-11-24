package lt.markmerkk.utils.os_formatter;

/**
 * Created by mariusmerkevicius on 11/24/15.
 */
public class OSXOutput implements IOSOutput {
  @Override public void onDurationMessage(String message) {
    if (message == null) return;
    // todo : needs tinkering, does not work out of the box
    //com.apple.eawt.Application.getApplication().setDockIconBadge(message);
  }
}
