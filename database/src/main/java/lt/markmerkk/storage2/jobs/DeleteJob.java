package lt.markmerkk.storage2.jobs;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import lt.markmerkk.storage2.database.annotations.Table;
import lt.markmerkk.storage2.database.helpers.DBQueryUpdate;
import lt.markmerkk.storage2.database.interfaces.DBEntity;
import lt.markmerkk.storage2.database.interfaces.DBIndexable;
import lt.markmerkk.storage2.database.interfaces.DBPackable;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * A job responsible for updating already existing row
 */
public class DeleteJob implements IQueryJob {

  private final Class clazz;
  private DBIndexable index;

  public DeleteJob(Class clazz, DBIndexable index) {
    if (clazz == null)
      throw new IllegalArgumentException("Cannot create job without a class");
    if (index == null)
      throw new IllegalArgumentException("Cannot create job without an index");
    this.index = index;
    this.clazz = clazz;
  }

  @Override public String query() {
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    return String.format("DELETE FROM %s WHERE %s", ((Table) tableAnnotation).name(),
        index.indexClause());
  }

  @Override public void execute(Connection connection) throws SQLException {
    connection.createStatement().execute(query());
  }
}
