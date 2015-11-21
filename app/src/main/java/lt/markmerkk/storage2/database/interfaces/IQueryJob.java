package lt.markmerkk.storage2.database.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A configuration that contains a query to be executed
 */
public interface IQueryJob {
  /**
   * Executes a statement on a valid connection
   * @param connection provided valid connection
   * @throws SQLException reports an error if occurs
   */
  void execute(Connection connection) throws SQLException;
}
