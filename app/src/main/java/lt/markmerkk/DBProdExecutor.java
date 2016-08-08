package lt.markmerkk;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.entities.database.DBBaseExecutor;
import lt.markmerkk.utils.UserSettingsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DBProdExecutor extends DBBaseExecutor {
  public static final Logger logger = LoggerFactory.getLogger(DBProdExecutor.class);

  @Inject
  UserSettingsImpl settings;

  @PostConstruct
  private void initialize() {
    logger.debug("Applying database migrations");
    if (settings.getVersion() <= 0) {
      logger.debug("Found unversioned database! Flushing database with new version.");
      settings.setVersion(Main.VERSION_CODE);
    }
    migrate();
  }

  @PreDestroy
  private void destroy() {
  }

  @Override
  protected String database() {
    return Main.CFG_PATH + "wt4_1.db";
  }

  @Override
  protected URI migrationScriptPath() {
    try {
      return getClass().getResource("/changelog_1.xml").toURI();
    } catch (URISyntaxException e) {
      return null;
    }
  }

  @Override
  protected URI migrationExportPath() {
    return Paths.get(Main.CFG_PATH).toUri();
  }

}
