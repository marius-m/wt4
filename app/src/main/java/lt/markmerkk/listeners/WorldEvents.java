package lt.markmerkk.listeners;

/**
 * Created by mariusmerkevicius on 12/21/15.
 * Represents world events, that are needed for this class to function properly
 */
public interface WorldEvents {
  /**
   * life-cycle event when starting class usage
   */
  void onStart();

  /**
   * life-cycle event when stopping class usage
   */
  void onStop();
}
