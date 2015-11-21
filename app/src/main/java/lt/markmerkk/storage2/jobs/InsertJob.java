package lt.markmerkk.storage2.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import lt.markmerkk.storage2.database.helpers.DBQueryCreate;
import lt.markmerkk.storage2.database.helpers.DBQueryInsert;
import lt.markmerkk.storage2.database.interfaces.DBInsertable;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.entities.SimpleLog;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for creating a new table for the {@link SimpleLog}
 */
public class InsertJob<T> implements IQueryJob {

  private final DBInsertable entity;
  private final Class<T> clazz;

  public InsertJob(Class<T> clazz, DBInsertable entity) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    if (entity == null)
      throw new IllegalArgumentException("Cannot create job without an instance");
    this.entity = entity;
    this.clazz = clazz;
  }

  @Override public void execute(Connection connection) throws SQLException {
    DBQueryInsert queryInsert = new DBQueryInsert();
    Statement statement = connection.createStatement();
    statement.execute(queryInsert.formQuery(clazz, entity));
  }
}
