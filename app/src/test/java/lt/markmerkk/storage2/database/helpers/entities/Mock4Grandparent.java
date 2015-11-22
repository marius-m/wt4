package lt.markmerkk.storage2.database.helpers.entities;

import java.util.LinkedHashMap;
import java.util.Map;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.interfaces.DBPackable;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock4") public class Mock4Grandparent implements DBPackable {
  @Column(value = FieldType.INTEGER) Long _id;

  public Mock4Grandparent(Long _id) {
    this._id = _id;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    return new LinkedHashMap<String, Object>() {{
      put("_id", _id);
    }};
  }
}
