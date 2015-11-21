package lt.markmerkk.storage2.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import lt.markmerkk.storage2.database.helpers.DBQueryCreateIfNotExist;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.entities.SimpleLog;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for creating a new table for the {@link SimpleLog}
 */
public class CreateJobIfNeeded<T> implements IQueryJob {

  Class<T> clazz;

  public CreateJobIfNeeded(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    this.clazz = clazz;
  }

  @Override public void execute(Connection connection) throws SQLException {
    DBQueryCreateIfNotExist creationHelper = new DBQueryCreateIfNotExist();
    Statement statement = connection.createStatement();
    statement.execute(creationHelper.formQuery(clazz));
  }
}
