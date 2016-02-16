package lt.markmerkk.storage2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lt.markmerkk.storage2.database.annotations.Column;
import lt.markmerkk.storage2.database.annotations.FieldType;
import lt.markmerkk.storage2.database.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/30/15.
 * Represents a jira issue
 */
@Table(name = "LocalIssue")
public class LocalIssue extends RemoteEntity {
  private static final String KEY_PROJECT = "project";
  private static final String KEY_KEY = "key";
  private static final String KEY_DESCRIPTION = "description";
  private static final String KEY_CREATE_DATE = "create_date";
  private static final String KEY_UPDATE_DATE = "update_date";

  @Column(value = FieldType.TEXT)
  String project;
  @Column(value = FieldType.TEXT)
  String key;
  @Column(value = FieldType.TEXT)
  String description;

  @Column(value = FieldType.INTEGER)
  String createDate;
  @Column(value = FieldType.INTEGER)
  String updateDate;

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
    pack.put(KEY_CREATE_DATE, "\"" + createDate + "\"");
    pack.put(KEY_UPDATE_DATE, "\"" + updateDate + "\"");
    return pack;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    super.unpack(resultSet);
    project = resultSet.getString(resultSet.findColumn(KEY_PROJECT));
    key = resultSet.getString(resultSet.findColumn(KEY_KEY));
    description = resultSet.getString(resultSet.findColumn(KEY_DESCRIPTION));
    createDate = resultSet.getString(resultSet.findColumn(KEY_CREATE_DATE));
    updateDate = resultSet.getString(resultSet.findColumn(KEY_UPDATE_DATE));
  }

  @Override public String toString() {
    return key + ": "+description;
  }
}
