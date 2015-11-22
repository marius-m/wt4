package lt.markmerkk.storage2.database;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Test database for various mock sqlite transactions
 */
public class DBMockExecutor extends DBBaseExecutor {

  public static final String FILE = "test_database.db";

  public DBMockExecutor() {
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
