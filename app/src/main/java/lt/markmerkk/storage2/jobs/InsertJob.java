package lt.markmerkk.storage2.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import lt.markmerkk.storage2.database.helpers.DBQueryInsert;
import lt.markmerkk.storage2.database.interfaces.DBPackable;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.entities.SimpleLog;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for creating a new table for the {@link SimpleLog}
 */
public class InsertJob implements IQueryJob {

  private final DBPackable entity;
  private final Class clazz;

  public InsertJob(Class clazz, DBPackable entity) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    if (entity == null)
      throw new IllegalArgumentException("Cannot create job without an instance");
    this.entity = entity;
    this.clazz = clazz;
  }

  @Override public String query() {
    return new DBQueryInsert().formQuery(clazz, entity);
  }

  @Override public void execute(Connection connection) throws SQLException {
    connection.createStatement().execute(query());
  }
}
