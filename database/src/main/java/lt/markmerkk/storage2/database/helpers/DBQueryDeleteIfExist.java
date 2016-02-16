package lt.markmerkk.storage2.database.helpers;

import java.lang.annotation.Annotation;
import lt.markmerkk.storage2.database.annotations.Column;
import lt.markmerkk.storage2.database.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBEntity;
import lt.markmerkk.storage2.database.interfaces.IQuery;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Deletes a table if such exists
 */
public class DBQueryDeleteIfExist implements IQuery {

  @Override public String formQuery(Class clazz) throws IllegalArgumentException {
    return formQuery(clazz, null);
  }

  @Override public String formQuery(Class clazz, DBEntity entity) throws IllegalArgumentException {
    if (clazz == null) throw new IllegalArgumentException("Cant create query for a null value!");
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation == null)
      throw new IllegalArgumentException("Provided class does not have @Table annotation!");
    return String.format("DROP TABLE IF EXISTS %s%s", ((Table) tableAnnotation).name());
  }
}
