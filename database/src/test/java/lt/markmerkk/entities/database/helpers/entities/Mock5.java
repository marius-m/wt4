package lt.markmerkk.entities.database.helpers.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import lt.markmerkk.entities.database.annotations.Column;
import lt.markmerkk.entities.database.annotations.FieldType;
import lt.markmerkk.entities.database.annotations.Table;
import lt.markmerkk.entities.database.interfaces.DBIndexUpdatable;
import lt.markmerkk.entities.database.interfaces.DBIndexable;
import lt.markmerkk.entities.database.interfaces.DBPackable;
import lt.markmerkk.entities.database.interfaces.DBUnpackable;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3")
public class Mock5 implements DBPackable, DBUnpackable, DBIndexable, DBIndexUpdatable {
  @Column(value = FieldType.INTEGER, isPrimary = true) long _id;
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;

  public Mock5() {}

  public Mock5(String title, String param) {
    this.title = title;
    this.param = param;
  }

  //region Getters / Setters

  public String getTitle() {
    return title;
  }

  public String getParam() {
    return param;
  }

  public long get_id() {
    return _id;
  }

  //endregion

  //region Database archiving functions

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
    hashMap.put("title", "\"" + title + "\"");
    hashMap.put("param", "\"" + param + "\"");
    return hashMap;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    this._id = resultSet.getLong(resultSet.findColumn("_id"));
    this.title = resultSet.getString(resultSet.findColumn("title"));
    this.param = resultSet.getString(resultSet.findColumn("param"));
  }

  @Override public String indexClause() {
    return "id = '"+_id+"'";
  }

  @Override public void updateIndex(long newIndex) {
    this._id = newIndex;
  }

  //endregion

}
