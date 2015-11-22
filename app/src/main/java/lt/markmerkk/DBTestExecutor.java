package lt.markmerkk;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import lt.markmerkk.storage2.database.DBBaseExecutor;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Test database for various mock sqlite transactions
 */
public class DBTestExecutor extends DBBaseExecutor {

  public static final String FILE = "test_database.db";

  public DBTestExecutor() {
    try {
      Path path = FileSystems.getDefault().getPath(FILE);
      boolean result = Files.deleteIfExists(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override protected String database() {
    return FILE;
  }
}
