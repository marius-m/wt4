package lt.markmerkk.storage2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lt.markmerkk.storage2.database.annotations.Column;
import lt.markmerkk.storage2.database.annotations.FieldType;
import lt.markmerkk.storage2.database.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Represents basic entity that could be used remotely
 */
@Table
public abstract class RemoteEntity extends BaseDBEntity {
  private static final String KEY_ID = "id";
  private static final String KEY_URI = "uri";

  private static final String KEY_DELETE = "deleted";
  private static final String KEY_DIRTY = "dirty";
  private static final String KEY_ERROR = "error";
  private static final String KEY_ERROR_MESSAGE = "errorMessage";

  // Server id
  @Column(value = FieldType.INTEGER)
  long id;
  // Server uri
  @Column(value = FieldType.TEXT)
  String uri;

  // Upload states

  // Delete
  @Column(value = FieldType.INTEGER)
  boolean deleted;
  // Needs update with the server
  @Column(value = FieldType.INTEGER)
  boolean dirty = true; // By default entity is always dirty
  // Error uploading
  @Column(value = FieldType.INTEGER)
  boolean error;
  // Error message
  @Column(value = FieldType.TEXT)
  String errorMessage;

  public String getUri() {
    return uri;
  }

  public long getId() {
    return id;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public boolean isDirty() {
    return dirty;
  }

  public boolean isError() {
    return error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    Map<String, Object> pack = super.pack();
    pack.put(KEY_ID, id);
    pack.put(KEY_URI, "\"" + uri + "\"");
    pack.put(KEY_DELETE, (deleted) ? 1 : 0);
    pack.put(KEY_DIRTY, (dirty) ? 1 : 0);
    pack.put(KEY_ERROR, (error) ? 1 : 0);
    pack.put(KEY_ERROR_MESSAGE, "\"" + errorMessage + "\"");
    return pack;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    super.unpack(resultSet);
    id = resultSet.getLong(resultSet.findColumn(KEY_ID));
    uri = resultSet.getString(resultSet.findColumn(KEY_URI));
    deleted = resultSet.getBoolean(resultSet.findColumn(KEY_DELETE));
    dirty = resultSet.getBoolean(resultSet.findColumn(KEY_DIRTY));
    error = resultSet.getBoolean(resultSet.findColumn(KEY_ERROR));
    errorMessage = resultSet.getString(resultSet.findColumn(KEY_ERROR_MESSAGE));
  }


}
