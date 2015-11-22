package lt.markmerkk.storage2.database.interfaces;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Represents the data that is passed down in where clause
 */
public interface DBIndexable extends DBEntity {
  String indexClause();
}
