package lt.markmerkk;

import java.io.File;
import java.io.IOException;
import lt.markmerkk.storage2.database.DBBaseExecutor;
import org.apache.commons.io.FileUtils;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DBProdExecutor extends DBBaseExecutor {

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
    //return "wt4.db";
  }
}
