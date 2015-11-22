package lt.markmerkk.storage2.database.helpers.entities;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3") public class Mock3NoPacking {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;
}
