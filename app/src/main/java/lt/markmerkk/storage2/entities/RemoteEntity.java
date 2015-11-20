package lt.markmerkk.storage2.entities;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Represents basic entity that could be used remotely
 */
@Table
public abstract class RemoteEntity extends BaseDBEntity {
  // Server id
  @Column(value = FieldType.INTEGER)
  long id;
  // Server uri
  @Column(value = FieldType.TEXT)
  String uri;

  // Upload states

  // Needs update with the server
  @Column(value = FieldType.INTEGER)
  boolean dirty;
  // Error uploading
  @Column(value = FieldType.INTEGER)
  boolean error;
  // Error message
  @Column(value = FieldType.TEXT)
  String errorMessage;
}
