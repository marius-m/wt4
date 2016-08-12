package lt.markmerkk.listeners;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents an interface, that indicates class can be destroyed.
 * Mainly used with MVP model for more control over presenter class.
 */
@Deprecated
public interface Destroyable {
  /**
   * Called when presenter is about to be destroyed
   */
  void destroy();
}
