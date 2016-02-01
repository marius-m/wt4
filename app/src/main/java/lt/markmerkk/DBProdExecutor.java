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
    return Main.CFG_PATH+"wt4.db";
  }

}
