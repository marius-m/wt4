package lt.markmerkk.storage2.database;

import java.io.File;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Helper function to hold database object
 */
public class DBInstance {

  public static final String DB_NAME = "test.db";
  protected SqlJetDb db;

  /**
   * Opens database file and creates necessary files
   * @throws SqlJetException
   */
  public void open() throws SqlJetException {
    if (db == null) throw new IllegalAccessError("Database is null!");
    if (db.isOpen()) throw new IllegalAccessError("Database is already open!");
    db = SqlJetDb.open(new File(DB_NAME), true);
  }

  /**
   * Closes database
   */
  public void close() throws SqlJetException {
    if (db != null) db.close();
    db = null;
  }

}
