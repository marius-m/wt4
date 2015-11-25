package lt.markmerkk;

import lt.markmerkk.storage2.database.DBBaseExecutor;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DBProdExecutor extends DBBaseExecutor {

  @Override protected String database() {
    String home = System.getProperty("user.home");
    if (home == null)
      return "wt4.db";
    //return home+"/wt4.db";
    return "wt4.db";
  }
}
