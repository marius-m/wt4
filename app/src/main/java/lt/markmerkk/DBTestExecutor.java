package lt.markmerkk;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lt.markmerkk.entities.database.DBBaseExecutor;

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
    migrate();
  }


  @Override
  protected String database() {
    return "test_database.db";
  }

  @Override
  protected URI migrationScriptPath() {
    return Paths.get("src/main/resources/" + "changelog_1.xml").toUri();
  }

  @Override
  protected URI migrationExportPath() {
    try {
      return getClass().getResource("/").toURI();
    } catch (URISyntaxException e) {
      return null;
    }
  }


}
