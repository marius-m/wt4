package lt.markmerkk.storage2.database;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.utils.Utils;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Forms a query for creating a table
 */
public class DBQueryCreateHelper implements IQueryHelper {

  /**
   * Creates a table in sqlite create annotated Entities.
   * Table must be annotated as {@link Table}
   * and all table columns must be identified as {@link Column}
   */
  @Override public String formQuery(Class clazz) {
    if (clazz == null) return null;
    String query = "CREATE TABLE ";
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation != null) {
      query += ((Table) tableAnnotation).name();
      query += formColumns(clazz);
      return query;
    }
    return null;
  }

  /**
   * Generates table creation SQL by the class model annotations
   * recursively
   * @param clazz provided class model
   * @return sql script
   */
  String formColumns(Class clazz) {
    if (clazz == null)
      return "";
    StringBuilder query = new StringBuilder();
    do {
      query = getFields(clazz, query);
      clazz = clazz.getSuperclass();
    } while(clazz != null);

    // Detele the last comma and wrap in brackets
    if (Utils.isEmpty(query.toString())) return "";
    query.deleteCharAt(query.length()-1);
    query.insert(0, " (");
    query.append(")");

    return query.toString();
  }

  /**
   * Gets all the annotated fields from the class
   * @param clazz provided class
   * @param query provided query builder
   * @return query builder
   */
  StringBuilder getFields(Class clazz, StringBuilder query) {
    if (query == null)
      throw new IllegalArgumentException("Query builder cannot be null!");
    if (clazz == null)
      return query;
    Field[] fields = clazz.getDeclaredFields();
    if (fields.length == 0)
      return query;
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      Column columnAnnotation = field.getAnnotation(Column.class);
      if (columnAnnotation == null) continue;

      query.append(field.getName());
      query.append(" " + columnAnnotation.value().name());
      if (columnAnnotation.isPrimary()) query.append(" PRIMARY KEY");
      if (columnAnnotation.defaultValue().length() > 0) {
        query.append(" default " + columnAnnotation.defaultValue());
      }
      if (i < fields.length - 1) query.append(",");
    }
    return query;
  }

  //endregion

}
