package lt.markmerkk.entities.database.helpers.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lt.markmerkk.entities.database.annotations.Column;
import lt.markmerkk.entities.database.annotations.FieldType;
import lt.markmerkk.entities.database.annotations.Table;
import lt.markmerkk.entities.database.interfaces.DBIndexable;
import lt.markmerkk.entities.database.interfaces.DBPackable;
import lt.markmerkk.entities.database.interfaces.DBUnpackable;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3") public class Mock3NullPacking implements DBPackable, DBIndexable, DBUnpackable {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;

  public Mock3NullPacking(String title, String param) {
    this.title = title;
    this.param = param;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    return null;
  }

  @Override public String indexClause() {
    return "title = '"+title+"'";
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {

  }
}