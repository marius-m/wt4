package lt.markmerkk.storage2.database.helpers;

import java.lang.annotation.Annotation;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBEntity;
import lt.markmerkk.storage2.database.interfaces.IQuery;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Forms a query for creating a table
 */
public class DBQueryCreateIfNotExist implements IQuery {

  /**
   * Creates a table in sqlite create annotated Entities.
   * Table must be annotated as {@link Table}
   * and all table columns must be identified as {@link Column}
   */
  @Override public String formQuery(Class clazz) throws IllegalArgumentException {
    return formQuery(clazz, null);
  }

  @Override public String formQuery(Class clazz, DBEntity entity) throws IllegalArgumentException {
    if (clazz == null) throw new IllegalArgumentException("Cant create query for a null value!");
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    return String.format("CREATE TABLE IF NOT EXISTS %s%s", ((Table) tableAnnotation).name(),
        DBQueryUtils.formColumns(clazz));
  }
}
