package lt.markmerkk.entities.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import lt.markmerkk.entities.database.helpers.DBQueryUpdate;
import lt.markmerkk.entities.database.interfaces.DBEntity;
import lt.markmerkk.entities.database.interfaces.DBPackable;
import lt.markmerkk.entities.database.interfaces.IQueryJob;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for updating already existing row
 */
public class UpdateJob implements IQueryJob {

  private final DBEntity entity;
  private final Class clazz;

  public UpdateJob(Class clazz, DBPackable entity) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    if (entity == null)
      throw new IllegalArgumentException("Cannot create job without an instance");
    this.entity = entity;
    this.clazz = clazz;
  }

  @Override public String query() {
    return new DBQueryUpdate().formQuery(clazz, entity);
  }

  @Override public void execute(Connection connection) throws SQLException {
    connection.createStatement().execute(query());
  }
}
