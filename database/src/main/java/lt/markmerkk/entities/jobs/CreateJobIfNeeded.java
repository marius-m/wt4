package lt.markmerkk.entities.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import lt.markmerkk.entities.database.helpers.DBQueryCreateIfNotExist;
import lt.markmerkk.entities.database.interfaces.IQueryJob;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for creating a new table
 */
public class CreateJobIfNeeded<T> implements IQueryJob {

  Class<T> clazz;

  public CreateJobIfNeeded(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    this.clazz = clazz;
  }

  @Override public String query() {
    return new DBQueryCreateIfNotExist().formQuery(clazz);
  }

  @Override public void execute(Connection connection) throws SQLException {
    connection.createStatement().execute(query());
  }
}
