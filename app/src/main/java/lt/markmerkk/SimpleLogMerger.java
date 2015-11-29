package lt.markmerkk;

import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.database.DBBaseExecutor;

/**
 * Created by mariusmerkevicius on 11/28/15.
 * Responsible for merging / updating database {@link SimpleLog} entities
 * with the remote ones.
 *
 * This class should follow these rules for merging
 * 1. Create new local log if there is not current one
 * 2. Update local log with the data from sever
 * 3. All pulled data should contain dirty = 0.
 *
 */
public class SimpleLogMerger {
  DBBaseExecutor executor;

}
