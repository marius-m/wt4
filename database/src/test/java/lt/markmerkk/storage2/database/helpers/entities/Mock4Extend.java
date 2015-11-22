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
@Table(name = "mock4") public class Mock4Extend
    extends Mock4Grandparent {
  public static final String KEY_ID = "id";
  public static final String KEY_PARAM = "parentParam";
  @Column(value = FieldType.INTEGER) Long id;
  @Column(value = FieldType.TEXT) String parentParam;

  public Mock4Extend() {}

  public Mock4Extend(Long _id, Long id, String parentParam) {
    super(_id);
    this.id = id;
    this.parentParam = parentParam;
  }

  public Long getId() {
    return id;
  }

  public String getParentParam() {
    return parentParam;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    Map<String, Object> pack = super.pack();
    pack.put(KEY_ID, id);
    pack.put(KEY_PARAM, "\"" + parentParam + "\"");
    return pack;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    super.unpack(resultSet);
    id = resultSet.getLong(resultSet.findColumn(KEY_ID));
    parentParam = resultSet.getString(resultSet.findColumn(KEY_PARAM));
  }
}
