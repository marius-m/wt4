package lt.markmerkk.storage2.jobs;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lt.markmerkk.storage2.database.annotations.Table;
import lt.markmerkk.storage2.database.helpers.DBQueryUtils;
import lt.markmerkk.storage2.database.interfaces.DBIndexable;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.database.interfaces.IResult;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Responsible for querying a concrete model
 */
public class QueryListJob<T> implements IQueryJob, IResult<List<T>> {
  private final Class<T> clazz;
  private final DBIndexable indexable;
  List<T> entities;

  public QueryListJob(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    this.clazz = clazz;
    this.indexable = null;
  }

  public QueryListJob(Class<T> clazz, DBIndexable indexable) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    this.clazz = clazz;
    this.indexable = indexable;
  }

  @Override public String query() {
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    if (indexable != null && indexable.indexClause() == null)
      throw new IllegalArgumentException("Indexable not implemented!");
    if (indexable != null && indexable.indexClause() != null)
      return String.format("SELECT * FROM %s WHERE %s;", ((Table) tableAnnotation).name(),
          indexable.indexClause());
    return String.format("SELECT * FROM %s;", ((Table) tableAnnotation).name());
  }

  @Override public void execute(Connection connection) throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery(query());
    entities = new ArrayList<>();
    while (resultSet.next()) {
      entities.add(DBQueryUtils.unwrapResult(clazz, resultSet));
    }
    resultSet.close();
  }

  @Override public List<T> result() {
    return entities;
  }
}
