package lt.markmerkk.storage2.database.helpers.entities;

import java.util.HashMap;
import java.util.Map;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBInsertable;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3") public class Mock3NoExtendNoPacking implements DBInsertable {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;

  public Mock3NoExtendNoPacking(String title, String param) {
    this.title = title;
    this.param = param;
  }

  @Override public Map<String, String> pack() throws IllegalArgumentException {
    return null;
  }
}
