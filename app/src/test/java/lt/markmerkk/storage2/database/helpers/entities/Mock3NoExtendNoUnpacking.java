package lt.markmerkk.storage2.database.helpers.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBUnpackable;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3") public class Mock3NoExtendNoUnpacking implements DBUnpackable {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;

  public Mock3NoExtendNoUnpacking(String title, String param) {
    this.title = title;
    this.param = param;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {

  }
}
