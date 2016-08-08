package lt.markmerkk.entities.database.interfaces;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Represents the data that is passed down in where clause
 */
public interface DBIndexable extends DBEntity {

  /**
   * Defines an index clause to make a query to
   * @return
   */
  String indexClause();
}
