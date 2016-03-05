package lt.markmerkk.storage2.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * An abstract class responsible for connecting to database
 */
public abstract class DBBaseExecutor implements IExecutor {

  private static final Logger logger = LoggerFactory.getLogger(DBBaseExecutor.class);

  Connection connection;

  public DBBaseExecutor() { }

  /**
   * Defines database name
   * @return
   */
  protected abstract String database();

  /**
   * Defines migration script name
   * @return
   */
  protected abstract String migrationScript();

  @Override
  public synchronized void executeOrThrow(IQueryJob queryJob)
      throws ClassNotFoundException, UnsupportedOperationException, IllegalArgumentException, SQLException {
    connection = open(database());
    if (connection == null) throw new IllegalArgumentException("connection == null");
    if (queryJob == null) throw new IllegalArgumentException("queryJob == null");
    executeQuery(queryJob, connection);
  }

  /**
   * Runs a migration sequence
   */
  protected void migrate() {
    try {
      Connection connection = open(database());
      Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
      String changeLogFilePath = getClass().getResource(migrationScript()).getPath();
      Liquibase liquibase = new Liquibase(
          changeLogFilePath,
          new FileSystemResourceAccessor(),
          database);
      liquibase.update(new Contexts(), new LabelExpression());
      close(connection);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (DatabaseException e) {
      e.printStackTrace();
    } catch (LiquibaseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Runs a database execution
   */
  public void execute(IQueryJob queryJob) {
    try {
      if (queryJob == null)
        throw new IllegalArgumentException("queryJob == null");
      logger.debug("Trying to run query: "+queryJob.query());
      executeOrThrow(queryJob);
    } catch (ClassNotFoundException e) { // Might throw when connecting to database
      logger.error("Cant connect to database!"+e.getMessage());
    } catch (UnsupportedOperationException e) { // Might throw when using wrong forming method
      logger.error("Unsupported operation! "+e.getMessage());
    } catch (IllegalArgumentException e) { // Might throw when forming
      logger.debug("Error! " + e.getMessage());
    } catch (SQLException e) { // Might throw with illegal queries
      logger.debug("Error! " + e.getMessage()); // this is perfectly normal
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
  public Connection open(String filename) throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    return DriverManager.getConnection("jdbc:sqlite:" + filename);
  }

  /**
   * Convenience method to close database connection
   *
   * @param connection provided database to close
   */
  public void close(Connection connection) {
    if (connection == null) return;
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //endregion
}
