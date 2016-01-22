package lt.markmerkk.listeners;

/**
 * Created by mariusmerkevicius on 11/29/15.
 * Responsible for merging local database entities with remote server
 */
public interface IMerger {
  /**
   * Merges tasks and prints out output
   * @return merge output
   */
  String merge();
}
