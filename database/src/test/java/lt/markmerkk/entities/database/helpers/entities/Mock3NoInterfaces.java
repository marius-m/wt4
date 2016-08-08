package lt.markmerkk.entities.database.helpers.entities;

import lt.markmerkk.entities.database.annotations.Column;
import lt.markmerkk.entities.database.annotations.FieldType;
import lt.markmerkk.entities.database.annotations.Table;
import lt.markmerkk.entities.database.interfaces.DBEntity;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3") public class Mock3NoInterfaces implements DBEntity {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;
  public Mock3NoInterfaces() {}
}
