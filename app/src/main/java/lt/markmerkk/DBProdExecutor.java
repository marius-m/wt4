package lt.markmerkk;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.storage2.database.DBBaseExecutor;
import lt.markmerkk.utils.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DBProdExecutor extends DBBaseExecutor {
  public static final Logger logger = LoggerFactory.getLogger(DBProdExecutor.class);

  @Inject
  UserSettings settings;

  @PostConstruct
  private void initialize() {
    logger.debug("Applying database migrations");
    if (settings.getVersion() <= 0) {
      logger.debug("Found unversioned database! Flushing database with new version.");
      // We have a case where database was not versioned.
      Path path = FileSystems.getDefault().getPath(database());
      try {
        boolean result = Files.deleteIfExists(path);
      } catch (IOException e) { }
      settings.setVersion(Main.VERSION_CODE);
    }
    migrate();
  }

  @PreDestroy
  private void destroy() { }

  @Override
  protected String database() {
    return Main.CFG_PATH + "wt4.db";
  }

  @Override
  protected String migrationScript() {
    return "/changelog_1.xml";
  }

}
