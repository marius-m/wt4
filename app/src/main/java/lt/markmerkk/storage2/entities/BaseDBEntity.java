package lt.markmerkk.storage2.entities;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Represents basic entity that could be used with database
 */
public abstract class BaseDBEntity {
  @Column(value = FieldType.INTEGER)
  long _id;
}
