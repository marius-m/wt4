package lt.markmerkk.storage2.jobs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lt.markmerkk.storage2.database.helpers.DBQueryInsert;
import lt.markmerkk.storage2.database.interfaces.DBEntity;
import lt.markmerkk.storage2.database.interfaces.DBIndexUpdatable;
import lt.markmerkk.storage2.database.interfaces.DBPackable;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for inserting a row
 */
public class InsertJob implements IQueryJob {

  private final DBEntity entity;
  private final Class clazz;

  public InsertJob(Class clazz, DBEntity entity) {
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
    Statement statement = connection.createStatement();
    statement.execute(query());
    ResultSet result = statement.getGeneratedKeys();
    if (entity instanceof DBIndexUpdatable)
      ((DBIndexUpdatable) entity).updateIndex(result.getInt(1)); // shour return row id
  }
}
