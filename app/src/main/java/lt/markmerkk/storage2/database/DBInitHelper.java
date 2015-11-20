package lt.markmerkk.storage2.database;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.Table;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Prepares database for work (initializes needed tables)
 */
public abstract class DBInitHelper extends DBInstance {

  protected abstract Class[] getTables();

  /**
   * Tries to create a table by the provided entity type
   * @throws SqlJetException
   */
  public void createTables() throws SqlJetException {
    open();
    for (final Class column : getTables()) {
      Table tableAnnotation = (Table) column.getAnnotation(Table.class);
      if (db.getSchema().getTable(tableAnnotation.name()) == null) continue;
      db.runWriteTransaction(new ISqlJetTransaction() {
        public Object run(SqlJetDb db) throws SqlJetException {
          db.createTable(formCreateQueryFromClass(column));
          return null;
        }
      });
    }
    close();
  }

  //region Convenience

  /**
   * Creates a table in sqlite create annotated Entities.
   * Table must be annotated as {@link Table}
   * and all table columns must be identified as {@link Column}
   * @param clazz
   * @return
   */
  private String formCreateQueryFromClass(Class clazz) {
    String query = "CREATE TABLE ";
    Annotation tableAnnotation = clazz.getAnnotation(Table.class);
    if (tableAnnotation != null) {
      query += ((Table) tableAnnotation).name() + " ";
      query += "(";
      query += generateCreationSQL(clazz);
      query += ");";
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
  private String generateCreationSQL(Class clazz) {
    if (clazz.getAnnotation(Table.class) == null) return null;
    StringBuilder query = new StringBuilder();
    Field[] fields = clazz.getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      if (field.getAnnotation(Column.class) != null) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        query.append(field.getName());
        query.append(" " + columnAnnotation.value().name());
        if (!columnAnnotation.canBeNull()) query.append(" NOT NULL");
        if (columnAnnotation.isPrimary()) query.append(" PRIMARY KEY");
        if (columnAnnotation.defaultValue().length() > 0) {
          query.append(" default " + columnAnnotation.defaultValue());
        }
        if (i < fields.length - 1) query.append(",");
      }
    }
    if (clazz.getSuperclass() != null) {
      String superColumns = generateCreationSQL(clazz.getSuperclass());
      if (superColumns != null) {
        query.insert(0, superColumns + ", ");
      }
    }
    return query.toString();
  }

  //endregion

}
