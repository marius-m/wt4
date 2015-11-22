package lt.markmerkk.storage2.jobs;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.helpers.DBQueryUtils;
import lt.markmerkk.storage2.database.interfaces.DBUnpackable;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import lt.markmerkk.storage2.database.interfaces.IResult;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Responsible for querying a concrete model
 */
public class QueryListJob<T> implements IQueryJob, IResult<List<T>> {
  private final Class<T> clazz;
  List<T> entities;

  public QueryListJob(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    this.clazz = clazz;
  }


  @Override public String query() {
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    return String.format("SELECT * FROM %s;", ((Table) tableAnnotation).name());
  }

  @Override public void execute(Connection connection) throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery(query());
    entities = new ArrayList<>();
    if (resultSet.getFetchSize() > 0) {
      resultSet.first();
      do {
        entities.add(DBQueryUtils.unwrapResult(clazz, resultSet));
      } while(resultSet.next());
    }
    resultSet.close();
  }

  @Override public List<T> result() {
    return entities;
  }
}
