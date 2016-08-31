package lt.markmerkk.entities.database.helpers;

import java.lang.annotation.Annotation;
import lt.markmerkk.entities.database.annotations.Column;
import lt.markmerkk.entities.database.annotations.Table;
import lt.markmerkk.entities.database.interfaces.DBEntity;
import lt.markmerkk.entities.database.interfaces.IQuery;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Forms a query for creating a table
 */
public class DBQueryCreate implements IQuery {

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
    return String.format("CREATE TABLE %s%s", ((Table) tableAnnotation).name(),
        DBQueryUtils.formColumnsFromClass(clazz));
  }
}