package lt.markmerkk.storage2.database.helpers.entities;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBEntity;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3") public class Mock3NoInterfaces implements DBEntity {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;
  public Mock3NoInterfaces() {}
}
