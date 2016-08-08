package lt.markmerkk.entities.jobs;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import lt.markmerkk.entities.database.annotations.Table;
import lt.markmerkk.entities.database.interfaces.DBIndexable;
import lt.markmerkk.entities.database.interfaces.IQueryJob;
import lt.markmerkk.entities.database.interfaces.IResult;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Responsible for querying a concrete model
 */
public class QueryDistinctListJob<T> implements IQueryJob, IResult<Set<String>> {
  private final Class<T> clazz;
  private final DBIndexable indexable;

  Set<String> entities;

  public QueryDistinctListJob(Class<T> clazz, DBIndexable indexable) {
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
    return String.format("SELECT DISTINCT %s FROM %s;", indexable.indexClause(), ((Table) tableAnnotation).name());
  }

  @Override public void execute(Connection connection) throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery(query());
    entities = new HashSet<>();
    while (resultSet.next())
      entities.add(resultSet.getString(resultSet.findColumn(indexable.indexClause())));
    resultSet.close();
  }

  @Override public Set<String> result() {
    return entities;
  }
}
