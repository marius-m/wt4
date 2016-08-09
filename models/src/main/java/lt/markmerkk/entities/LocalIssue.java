package lt.markmerkk.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lt.markmerkk.entities.database.annotations.Column;
import lt.markmerkk.entities.database.annotations.FieldType;
import lt.markmerkk.entities.database.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/30/15.
 * Represents a jira issue
 */
@Table(name = "LocalIssue")
public class LocalIssue extends RemoteEntity {
  public static final String KEY_PROJECT = "project";
  public static final String KEY_KEY = "key";
  public static final String KEY_DESCRIPTION = "description";
  public static final String KEY_CREATE_DATE = "createDate";
  public static final String KEY_UPDATE_DATE = "updateDate";

  @Column(value = FieldType.TEXT)
  String project;
  @Column(value = FieldType.TEXT)
  String key;
  @Column(value = FieldType.TEXT)
  String description;

  @Column(value = FieldType.INTEGER)
  long createDate;
  @Column(value = FieldType.INTEGER)
  long updateDate;

  // Life cycle links

  public String getKey() {
    return key;
  }

  public String getProject() {
    return project;
  }

  public String getDescription() {
    return description;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    Map<String, Object> pack = super.pack();
    pack.put(KEY_PROJECT, "\"" + project + "\"");
    pack.put(KEY_KEY, "\"" + key + "\"");
    pack.put(KEY_DESCRIPTION, "\"" + description + "\"");
    pack.put(KEY_CREATE_DATE, createDate);
    pack.put(KEY_UPDATE_DATE, updateDate);
    return pack;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    super.unpack(resultSet);
    project = resultSet.getString(resultSet.findColumn(KEY_PROJECT));
    key = resultSet.getString(resultSet.findColumn(KEY_KEY));
    description = resultSet.getString(resultSet.findColumn(KEY_DESCRIPTION));
    createDate = resultSet.getLong(resultSet.findColumn(KEY_CREATE_DATE));
    updateDate = resultSet.getLong(resultSet.findColumn(KEY_UPDATE_DATE));
  }

  @Override public String toString() {
    return key + ": "+description;
  }
}
