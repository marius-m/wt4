package lt.markmerkk.storage2.database.interfaces;

import java.util.Map;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Represents an entity that can be *packed* and
 * saved/updated in database
 */
public interface DBInsertable extends DBEntity {
  /**
   * Packs parameters and returns them as key/values
   * @return
   * @throws IllegalArgumentException
   */
  Map<String, Object> pack() throws IllegalArgumentException;
}