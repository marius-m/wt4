package lt.markmerkk.entities.database.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A configuration that contains a query to be executed
 */
public interface IQueryJob {
  /**
   * Query that is sent for execution
   * @return query
   */
  String query();

  /**
   * Executes a statement on a valid connection
   * @param connection provided valid connection
   * @throws SQLException reports an error if occurs
   */
  void execute(Connection connection) throws SQLException;
}
