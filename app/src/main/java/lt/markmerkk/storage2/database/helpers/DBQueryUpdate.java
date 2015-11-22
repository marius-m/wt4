package lt.markmerkk.storage2.database.helpers;

import java.lang.annotation.Annotation;
import java.util.Map;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBEntity;
import lt.markmerkk.storage2.database.interfaces.DBIndexable;
import lt.markmerkk.storage2.database.interfaces.DBPackable;
import lt.markmerkk.storage2.database.interfaces.IQuery;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Forms a query for updateig a row
 */
public class DBQueryUpdate implements IQuery {

  /**
   * Creates a table in sqlite create annotated Entities.
   * Table must be annotated as {@link Table}
   * and all table columns must be identified as {@link Column}
   */
  @Override public String formQuery(Class clazz) throws IllegalArgumentException {
    throw new UnsupportedOperationException("This query needs an object instance!");
  }

  @Override public String formQuery(Class clazz, DBEntity entity)
      throws IllegalArgumentException, UnsupportedOperationException {
    if (clazz == null) throw new IllegalArgumentException("Cant create query for a null value!");
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    if (!DBPackable.class.isAssignableFrom(clazz))
      throw new IllegalArgumentException("Provided class does not implement DBPackable!");
    if (!DBIndexable.class.isAssignableFrom(clazz))
      throw new IllegalArgumentException("Provided class does not implement DBIndexable!");
    Map<String, Object> pack = ((DBPackable) entity).pack();
    if (pack == null)
      throw new IllegalArgumentException("Cannot form columns without a map!");
    String whereClause = ((DBIndexable) entity).indexClause();
    if (whereClause == null)
      throw new IllegalArgumentException("Cannot form query without a index clause!");
    return String.format("UPDATE %s SET %s WHERE %s",
        ((Table) tableAnnotation).name(),
        DBQueryUtils.formKeyValuesFromMap(pack),
        whereClause);
  }
}
