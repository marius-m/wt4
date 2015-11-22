package lt.markmerkk.storage2.database.interfaces;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * An interface that defines a method that is called with new is suggested
 * after sql action (like insert that represents a new row id)
 */
public interface DBIndexUpdatable extends DBEntity {

  /**
   * Called when new row index ir proposed
   * @param newIndex proposed index id
   */
  void updateIndex(long newIndex);
}
