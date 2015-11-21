package lt.markmerkk.storage2.database.helpers;

import java.lang.reflect.Field;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.utils.Utils;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Static utils helper class for forming queries
 */
public class DBQueryUtils {
  /**
   * Generates table creation SQL by the class model annotations
   * recursively
   *
   * @param clazz provided class model
   * @return sql script
   */
  public static String formColumns(Class clazz) throws IllegalArgumentException {
    if (clazz == null) throw new IllegalArgumentException("Cannot form columns without a class!");
    StringBuilder query = new StringBuilder();
    do {
      query.append(getFields(clazz));
      clazz = clazz.getSuperclass();
    } while (clazz != null);

    // Detele the last comma and wrap in brackets
    if (Utils.isEmpty(query.toString())) return "";
    query.deleteCharAt(query.length() - 1);
    query.insert(0, " (");
    query.append(")");

    return query.toString();
  }

  /**
   * Gets all the annotated fields from the class
   *
   * @param clazz provided class
   * @return query builder
   */
  // We *still* dont provide core access to the utility class
  static String getFields(Class clazz) {
    if (clazz == null) throw new IllegalArgumentException("Query builder cannot be null!");
    StringBuilder query = new StringBuilder();
    Field[] fields = clazz.getDeclaredFields();
    if (fields.length == 0) return "";
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      Column columnAnnotation = field.getAnnotation(Column.class);
      if (columnAnnotation == null) continue;

      query.append(field.getName());
      query.append(" ");
      query.append(columnAnnotation.value().name());
      if (columnAnnotation.isPrimary()) query.append(" PRIMARY KEY");
      if (columnAnnotation.defaultValue().length() > 0) {
        query.append(" default ");
        query.append(columnAnnotation.defaultValue());
      }
      if (i < fields.length - 1) query.append(",");
    }
    return query.toString();
  }

}
