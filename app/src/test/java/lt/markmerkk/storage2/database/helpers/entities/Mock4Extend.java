package lt.markmerkk.storage2.database.helpers.entities;

import java.util.Map;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock4") public class Mock4Extend
    extends Mock4Grandparent {
  @Column(value = FieldType.INTEGER) String id;
  @Column(value = FieldType.TEXT) String parent_param;

  public Mock4Extend(String _id, String id, String parent_param) {
    super(_id);
    this.id = id;
    this.parent_param = parent_param;
  }

  @Override public Map<String, String> pack() throws IllegalArgumentException {
    Map<String, String> pack = super.pack();
    pack.put("id", id);
    pack.put("parent_param", "\"" + parent_param + "\"");
    return pack;
  }
}
