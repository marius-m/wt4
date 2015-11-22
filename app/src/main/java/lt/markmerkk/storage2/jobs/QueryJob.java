package lt.markmerkk.storage2.jobs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import lt.markmerkk.storage2.database.interfaces.DBUnpackable;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.database.interfaces.IResult;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class QueryJob<T> implements IQueryJob, IResult<T> {
  private final Class<T> clazz;
  T entity;

  public QueryJob(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    this.clazz = clazz;
  }


  @Override public String query() {
    return "SELECT * FROM mock4;";
  }

  @Override public void execute(Connection connection) throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery(query());
    unwrapResult(resultSet);
    resultSet.close();
  }

  // fixme : incomplete
  void unwrapResult(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    if (resultSet == null) throw new IllegalArgumentException("ResultSet is invalid!");
    try {
      entity = clazz.newInstance();
      if (!(entity instanceof DBUnpackable)) throw new IllegalArgumentException("Provided entity is not unpackable!");
      DBUnpackable unpackable = (DBUnpackable) entity;
      unpackable.unpack(resultSet);
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("Provided entity model cant be created!");
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Provided entity model cant be accessed!");
    }
  }

  @Override public T result() {
    return entity;
  }
}
