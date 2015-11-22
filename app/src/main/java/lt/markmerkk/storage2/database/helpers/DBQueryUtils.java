package lt.markmerkk.storage2.database.helpers;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage2.database.interfaces.DBUnpackable;
import lt.markmerkk.utils.Utils;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Static utils helper class for forming queries
 */
public class DBQueryUtils {

  /**
   * Unwraps a result set into an entity with concrete data
   * @param resultSet provided result set
   * @return entity
   * @throws IllegalArgumentException
   * @throws SQLException
   */
  public static <T>T unwrapResult(Class<T> clazz, ResultSet resultSet) throws IllegalArgumentException, SQLException {
    if (clazz == null) throw new IllegalArgumentException("Class is invalid!");
    if (resultSet == null) throw new IllegalArgumentException("ResultSet is invalid!");
    try {
      T entity = clazz.newInstance();
      if (!(entity instanceof DBUnpackable)) throw new IllegalArgumentException("Provided entity is not unpackable!");
      DBUnpackable unpackable = (DBUnpackable) entity;
      unpackable.unpack(resultSet);
      return entity;
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("Provided entity model cant be created!");
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Provided entity model cant be accessed!");
    }
  }

  /**
   * Generates column
   *
   * @return sql script
   */
  public static String formKeyValuesFromMap(Map<String, Object> map) throws IllegalArgumentException {
    if (map == null) throw new IllegalArgumentException("Cannot form columns without a map!");
    if (map.size() == 0) throw new IllegalArgumentException("Cannot form columns with an empty map!");
    StringBuilder query = new StringBuilder();
    for (String s : map.keySet()) {
      if (Utils.isEmpty(s)) continue;
      Object object = map.get(s);
      if (object == null) continue;
      if (object instanceof String && Utils.isEmpty((String)object)) continue;
      query.append(s);
      query.append("=");
      query.append(object);
      query.append(",");
    }

    // Detele the last comma and wrap in brackets
    query.deleteCharAt(query.length() - 1);
    query.insert(0, "(");
    query.append(")");

    return query.toString();
  }


  /**
   * Generates column
   *
   * @return sql script
   */
  public static String formColumnsFromMapKeys(Map<String, Object> map) throws IllegalArgumentException {
    if (map == null) throw new IllegalArgumentException("Cannot form columns without a map!");
    if (map.size() == 0) throw new IllegalArgumentException("Cannot form columns with an empty map!");
    StringBuilder query = new StringBuilder();
    for (String s : map.keySet()) {
      if (Utils.isEmpty(s)) continue;
      Object object = map.get(s);
      if (object == null) continue;
      if (object instanceof String && Utils.isEmpty((String)object)) continue;
      query.append(s);
      query.append(",");
    }

    // Detele the last comma and wrap in brackets
    query.deleteCharAt(query.length() - 1);
    query.insert(0, "(");
    query.append(")");

    return query.toString();
  }

  /**
   * Generates column
   *
   * @return sql script
   */
  public static String formColumnsFromMapValues(Map<String, Object> map) throws IllegalArgumentException {
    if (map == null) throw new IllegalArgumentException("Cannot form columns without a map!");
    if (map.size() == 0) throw new IllegalArgumentException("Cannot form columns with an empty map!");
    StringBuilder query = new StringBuilder();
    for (String s : map.keySet()) {
      if (Utils.isEmpty(s)) continue;
      if (!map.containsKey(s)) continue;
      Object object = map.get(s);
      if (object == null) continue;
      if (object instanceof String && Utils.isEmpty((String)object)) continue;
      query.append(map.get(s));
      query.append(",");
    }

    // Detele the last comma and wrap in brackets
    query.deleteCharAt(query.length() - 1);
    query.insert(0, "(");
    query.append(")");

    return query.toString();
  }

  /**
   * Generates SQL ready column gathering from the class model
   *
   * @param clazz provided class model
   * @return sql
   */
  public static String formColumnsFromClass(Class clazz) throws IllegalArgumentException {
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
   * @return list of fields
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
      query.append(",");
    }
    return query.toString();
  }

}
