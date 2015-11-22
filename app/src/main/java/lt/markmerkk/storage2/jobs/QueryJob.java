package lt.markmerkk.storage2.jobs;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.annotations.Index;
import lt.markmerkk.storage2.database.helpers.DBQueryUtils;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.database.interfaces.IResult;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Responsible for querying a concrete model
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
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    //Annotation indexAnnotation = clazz.getAnnotation(Index.class);
    //if (indexAnnotation == null)
    //  throw new IllegalArgumentException("Provided class does not specify and index to look up to!");
    return String.format("SELECT * FROM %s;", ((Table) tableAnnotation).name());
  }

  @Override public void execute(Connection connection) throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery(query());
    entity = DBQueryUtils.unwrapResult(clazz, resultSet);
    resultSet.close();
  }

  @Override public T result() {
    return entity;
  }
}
