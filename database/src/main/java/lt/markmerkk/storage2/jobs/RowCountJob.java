package lt.markmerkk.storage2.jobs;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import lt.markmerkk.storage2.database.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.database.interfaces.IResult;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Responsible for querying a concrete model
 */
public class RowCountJob<T> implements IQueryJob, IResult<Integer> {
  private final Class<T> clazz;
  private Integer result;

  public RowCountJob(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    this.clazz = clazz;
  }

  @Override public String query() {
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    return String.format("SELECT COUNT(*) FROM %s", ((Table) tableAnnotation).name());
  }

  @Override public void execute(Connection connection) throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery(query());
    if (!resultSet.next()) throw new IllegalArgumentException("Error retrieving object!");
    result = resultSet.getInt(1); // Should be the first one
    resultSet.close();
  }

  @Override public Integer result() {
    return result;
  }
}
