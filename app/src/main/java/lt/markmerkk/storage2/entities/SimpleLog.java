package lt.markmerkk.storage2.entities;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Represents a worklog entity
 */
@Table(name = "Log")
public class SimpleLog extends RemoteEntity {
  @Column(value = FieldType.INTEGER)
  long start;
  @Column(value = FieldType.INTEGER)
  long end;
  @Column(value = FieldType.TEXT)
  String task;
  @Column(value = FieldType.TEXT)
  String comment;
}
