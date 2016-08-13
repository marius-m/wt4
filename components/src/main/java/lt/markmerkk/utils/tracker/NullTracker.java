package lt.markmerkk.utils.tracker;

/**
 * Created by mariusmerkevicius on 3/11/16.
 * Empty {@link ITracker} that does not do anything.
 * Mainly used for debugging
 */
public class NullTracker implements ITracker {

  @Override
  public void sendEvent(String category, String action, String label, int value) { }

  @Override
  public void sendEvent(String category, String action) { }

  @Override
  public void sendView(String contentDescription) { }

  @Override
  public void stop() { }
}
