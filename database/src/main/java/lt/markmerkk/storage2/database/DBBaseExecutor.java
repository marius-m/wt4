package lt.markmerkk.storage2.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

  //region Abstract

  /**
   * Defines database name
   * @return
   */
  protected abstract String database();

  /**
   * Defines migration script name
   * @return
   */
  protected abstract URI migrationScriptPath();

  /**
   * Defines migration storage path
   * @return
   */
  protected abstract URI migrationExportPath();

  //endregion

  @Override
  public synchronized void executeOrThrow(IQueryJob queryJob)
      throws ClassNotFoundException, UnsupportedOperationException, IllegalArgumentException, SQLException {
    connection = open(database());
    if (connection == null) throw new IllegalArgumentException("connection == null");
    if (queryJob == null) throw new IllegalArgumentException("queryJob == null");
    executeQuery(queryJob, connection);
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

  //region Migration convenience functions

  /**
   * Runs a migration sequence
   */
  protected void migrate() {
    try {
      Connection connection = open(database());
      Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

      logger.debug("Running migration from: " + migrationScriptPath());
      logger.debug("Running migration to: " + migrationExportPath().getPath());

      // Extracting migrations
      String targetMigrationFilePath = migrationScriptPath().toString().contains("jar")
          ? extractMigrationsFromJar() : extractMigrationsFromFilesystem();
      if (targetMigrationFilePath == null)
        throw new IllegalArgumentException("Migration file cannot be found!");
      Liquibase liquibase = new Liquibase(
          targetMigrationFilePath,
          new FileSystemResourceAccessor(),
          database);
      liquibase.update(new Contexts(), new LabelExpression());
      close(connection);
    } catch (ClassNotFoundException e) {
      logger.error("Migration error! ", e);
    } catch (SQLException e) {
      logger.error("Migration error! ", e);
    } catch (DatabaseException e) {
      logger.error("Migration error! ", e);
    } catch (LiquibaseException e) {
      logger.error("Migration error! ", e);
    }
  }

  /**
   * Extracts migrations from regular file system.
   * @return
   * @throws IOException
   */
  String extractMigrationsFromFilesystem() {
    try {
      String targetMigration;
      Path migrationScriptFile = Paths.get(migrationScriptPath());
      Path targetFile = Paths.get(migrationExportPath().getPath() + migrationScriptFile.getFileName());
      Files.deleteIfExists(targetFile);
      Files.copy(migrationScriptFile, targetFile);
      targetMigration = targetFile.toString();
      return targetMigration;
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Extracts migration files from jar archive
   * @return
   */
  String extractMigrationsFromJar() {
    InputStream is = null;
    FileOutputStream fos = null;
    try {
      JarFile jar = new JarFile(jarPathFromUri(migrationScriptPath().toString()));
      Enumeration enumEntries = jar.entries();
      while (enumEntries.hasMoreElements()) {
        JarEntry jarEntry = (JarEntry) enumEntries.nextElement();
        if (!jarEntry.getName().contains("changelog_1.xml")) continue;
        is = jar.getInputStream(jarEntry);
        fos = new FileOutputStream(new File(migrationExportPath().getPath() + jarEntry.getName()));
        String targetMigration = migrationExportPath().getPath() + jarEntry.getName();
        while (is.available() > 0)
          fos.write(is.read());
        return targetMigration;
      }
    } catch (IOException e) {
      logger.error("Error extracting migrations to dir!", e);
    } finally {
      try {
        if (is != null)
          is.close();
        if (fos != null)
          fos.close();
      } catch (IOException e) { }
    }
    return null;
  }

  /**
   * Extracts raw jar path from specified uri
   * @param rawPath
   * @return
   */
  static String jarPathFromUri(String rawPath) {
    if (rawPath == null)
      throw new IllegalArgumentException("Error extracting jar!");
    try {
      rawPath = rawPath.replaceAll("jar:", "");
      URI fileUri = new URI(rawPath.split("!")[0]);
      return fileUri.getPath();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Malformed uri!");
    }
  }

  //endregion

}
