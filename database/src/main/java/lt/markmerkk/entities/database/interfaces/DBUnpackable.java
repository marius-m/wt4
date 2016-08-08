package lt.markmerkk.entities.database.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Represents an entity that can be unpacked from database into an object.
 */
public interface DBUnpackable extends DBEntity {
  /**
   * Unpacks result from database into an entity
   * @param resultSet result from database
   * @return
   */
  void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException;
}
