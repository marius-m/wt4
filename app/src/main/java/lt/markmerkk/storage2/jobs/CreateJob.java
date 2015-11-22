package lt.markmerkk.storage2.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import lt.markmerkk.storage2.database.helpers.DBQueryCreate;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.entities.SimpleLog;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for creating a new table for the {@link SimpleLog}
 */
public class CreateJob<T> implements IQueryJob {

  Class<T> clazz;

  public CreateJob(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    this.clazz = clazz;
  }

  @Override public String query() {
    return new DBQueryCreate().formQuery(clazz);
  }

  @Override public void execute(Connection connection) throws SQLException {
    connection.createStatement().execute(query());
  }
}
