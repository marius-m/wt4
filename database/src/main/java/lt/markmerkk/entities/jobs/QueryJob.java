package lt.markmerkk.entities.jobs;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import lt.markmerkk.entities.database.annotations.Table;
import lt.markmerkk.entities.database.helpers.DBQueryUtils;
import lt.markmerkk.entities.database.interfaces.DBIndexable;
import lt.markmerkk.entities.database.interfaces.IQueryJob;
import lt.markmerkk.entities.database.interfaces.IResult;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Responsible for querying a concrete model
 */
public class QueryJob<T> implements IQueryJob, IResult<T> {
  private final Class<T> clazz;
  private DBIndexable index;
  private T entity;

  public QueryJob(Class<T> clazz, DBIndexable indexClause) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    if (indexClause == null)
      throw new IllegalArgumentException("Cannot create job without an index");
    this.index = indexClause;
    this.clazz = clazz;
  }

  @Override public String query() {
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    return String.format("SELECT * FROM %s WHERE %s", ((Table) tableAnnotation).name(),
        index.indexClause());
  }

  @Override public void execute(Connection connection) throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery(query());
    if (!resultSet.next()) throw new IllegalArgumentException("Error retrieving object!");
    entity = DBQueryUtils.unwrapResult(clazz, resultSet);
    resultSet.close();
  }

  @Override public T result() {
    return entity;
  }
}
