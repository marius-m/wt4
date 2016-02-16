package lt.markmerkk.storage2.database.interfaces;

import java.sql.SQLException;

/**
 * Created by mariusmerkevicius on 11/29/15.
 * An executor instance that connects database and query jobs
 */
public interface IExecutor {
  /**
   * Executes a {@link IQuery} job
   * @param queryJob input query
   */
  void execute(IQueryJob queryJob);

  /**
   * Executes a {@link IQuery} job
   *
   * @param queryJob input query
   */
  void executeOrThrow(IQueryJob queryJob) throws ClassNotFoundException, UnsupportedOperationException,
      IllegalArgumentException, SQLException;
}
