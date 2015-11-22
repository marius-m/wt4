package lt.markmerkk.storage2.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import lt.markmerkk.storage2.database.annotations.Column;
import lt.markmerkk.storage2.database.annotations.FieldType;
import lt.markmerkk.storage2.database.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBIndexable;
import lt.markmerkk.storage2.database.interfaces.DBPackable;
import lt.markmerkk.storage2.database.interfaces.DBUnpackable;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Represents basic entity that could be used with database
 */
@Table
public abstract class BaseDBEntity implements DBIndexable, DBPackable, DBUnpackable {
  public static final String KEY_ID = "_id";

  @Column(value = FieldType.INTEGER, isPrimary = true)
  long _id;

  @Override public String indexClause() {
    return "_id = '" + _id + "'";
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    return new LinkedHashMap<String, Object>();
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    _id = resultSet.getLong(resultSet.findColumn(KEY_ID));
  }


}
