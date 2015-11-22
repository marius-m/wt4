package lt.markmerkk.storage2.database.helpers.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBPackable;
import lt.markmerkk.storage2.database.interfaces.DBUnpackable;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock4") public class Mock4Grandparent implements DBPackable, DBUnpackable {
  public static final String KEY_ID = "_id";
  @Column(value = FieldType.INTEGER) Long _id;

  public Mock4Grandparent() {}

  public Mock4Grandparent(Long _id) {
    this._id = _id;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    return new LinkedHashMap<String, Object>() {{
      put(KEY_ID, _id);
    }};
  }

  public Long get_id() {
    return _id;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    _id = resultSet.getLong(resultSet.findColumn(KEY_ID));
  }
}
