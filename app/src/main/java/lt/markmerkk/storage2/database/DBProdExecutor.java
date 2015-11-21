package lt.markmerkk.storage2.database;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Production database, used for real work
 */
public class DBProdExecutor extends DBBaseExecutor {
  @Override String database() {
    return "wt4.db";
  }
}
