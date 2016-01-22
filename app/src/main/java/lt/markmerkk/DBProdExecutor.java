package lt.markmerkk;

import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lt.markmerkk.storage2.SimpleIssue;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.database.DBBaseExecutor;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;
import org.apache.commons.io.FileUtils;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DBProdExecutor extends DBBaseExecutor {


  @PostConstruct
  private void initialize() {
    System.out.println("Creating necessary tables...");
    execute(new CreateJobIfNeeded<>(SimpleLog.class));
    execute(new CreateJobIfNeeded<>(SimpleIssue.class));
  }

  @PreDestroy
  private void destroy() { }

  @Override protected String database() {
    String home = System.getProperty("user.home");
    if (home == null)
      return "wt4.db";
    try {
      File file = new File(home+"/.wt4/");
      FileUtils.forceMkdir(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return home+"/.wt4/wt4.db";
//    return "wt4.db";
  }

}
