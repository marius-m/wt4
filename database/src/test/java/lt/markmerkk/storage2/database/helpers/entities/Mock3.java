package lt.markmerkk.storage2.database.helpers.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import lt.markmerkk.storage2.database.annotations.Column;
import lt.markmerkk.storage2.database.annotations.FieldType;
import lt.markmerkk.storage2.database.annotations.Index;
import lt.markmerkk.storage2.database.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBIndexable;
import lt.markmerkk.storage2.database.interfaces.DBPackable;
import lt.markmerkk.storage2.database.interfaces.DBUnpackable;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3")
public class Mock3 implements DBPackable, DBUnpackable, DBIndexable {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.INTEGER) String param;

  public Mock3() {}

  public Mock3(String title, String param) {
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

  //endregion

  //region Database archiving functions

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

  @Override public String indexClause() {
    return "title = '"+title+"'";
  }

  //endregion

  @Override public String toString() {
    return "Mock3{" +
        "title='" + title + '\'' +
        ", param='" + param + '\'' +
        '}';
  }

}
