package lt.markmerkk.storage2.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * An abstract class responsible for connecting to database
 */
public abstract class DBBaseExecutor {


  abstract String database();

  /**
   * Runs a database execution
   */
  public void execute(IQueryJob queryJob) {
    Connection connection = null;
    try {
      connection = open(database());
      if (connection == null) return;
      if (queryJob == null) return;
      executeQuery(queryJob, connection);
    } catch (ClassNotFoundException e) { // Might throw when connecting to database
      e.printStackTrace();
    } catch (UnsupportedOperationException e) { // Might throw when using wrong forming method
      e.printStackTrace();
    } catch (IllegalArgumentException e) { // Might throw when forming
      e.printStackTrace();
    } catch (SQLException e) { // Might throw with illegal queries
      e.printStackTrace();
    } finally {
      close(connection); // We close connection anyway
    }
  }

  //region Convenience

  /**
   * Main method to execute a query
   * @param queryJob provided query to execute
   * @param connection database connection
   * @throws SQLException
   */
  void executeQuery(IQueryJob queryJob, Connection connection) throws SQLException {
    queryJob.execute(connection);
  }

  /**
   * A convenience method to open database connection
   * @param filename provided database file to open
   * @return connection reference
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  Connection open(String filename) throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    return DriverManager.getConnection("jdbc:sqlite:" + filename);
  }

  /**
   * Convenience method to close database connection
   *
   * @param connection provided database to close
   */
  void close(Connection connection) {
    if (connection == null) return;
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //endregion
}
