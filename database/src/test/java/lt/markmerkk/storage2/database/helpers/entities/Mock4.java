package lt.markmerkk.storage2.database.helpers.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lt.markmerkk.storage2.database.annotations.Column;
import lt.markmerkk.storage2.database.annotations.FieldType;
import lt.markmerkk.storage2.database.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock4") public class Mock4 extends Mock4Extend {
  public static final String KEY_TITLE = "title";
  public static final String KEY_NAME = "name";
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.TEXT) String name;

  public Mock4() {}

  public Mock4(Long _id, Long id, String parentParam, String title, String name) {
    super(_id, id, parentParam);
    this.title = title;
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public String getName() {
    return name;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    Map<String, Object> pack = super.pack();
    pack.put(KEY_TITLE, "\"" + title + "\"");
    pack.put(KEY_NAME, "\"" + name + "\"");
    return pack;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    super.unpack(resultSet);
    title = resultSet.getString(resultSet.findColumn(KEY_TITLE));
    name = resultSet.getString(resultSet.findColumn(KEY_NAME));
  }
}
