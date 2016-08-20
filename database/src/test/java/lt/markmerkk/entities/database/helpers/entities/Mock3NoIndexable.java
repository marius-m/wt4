package lt.markmerkk.entities.database.helpers.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import lt.markmerkk.entities.database.annotations.Column;
import lt.markmerkk.entities.database.annotations.FieldType;
import lt.markmerkk.entities.database.annotations.Table;
import lt.markmerkk.entities.database.interfaces.DBPackable;
import lt.markmerkk.entities.database.interfaces.DBUnpackable;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3") public class Mock3NoIndexable implements DBPackable, DBUnpackable {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
    hashMap.put("title", "\"" + title + "\"");
    hashMap.put("param", "\"" + param + "\"");
    return hashMap;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    this.title = resultSet.getString(resultSet.findColumn("title"));
    this.param = resultSet.getString(resultSet.findColumn("param"));
  }
}
