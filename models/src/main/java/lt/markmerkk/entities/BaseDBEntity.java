package lt.markmerkk.entities;

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
 * Created by mariusmerkevicius on 11/20/15.
 * Represents basic entity that could be used with database
 */
@Table
public abstract class BaseDBEntity implements DBIndexable, DBPackable, DBUnpackable,
    DBIndexUpdatable {
  public static final String KEY_ID = "_id";

  @Column(value = FieldType.INTEGER, isPrimary = true)
  long _id;

  public long get_id() {
    return _id;
  }

  @Override public String indexClause() {
    return "_id = '" + _id + "'";
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    return new LinkedHashMap<String, Object>();
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    _id = resultSet.getLong(resultSet.findColumn(KEY_ID));
  }

  /**
   * This is called automatically. It should never be called by hand on any entity object!!!
   * @param newIndex proposed index id
   */
  @Override public void updateIndex(long newIndex) {
    this._id = newIndex;
  }
}
