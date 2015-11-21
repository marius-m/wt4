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
  @Column(value = FieldType.INTEGER) Long id;
  @Column(value = FieldType.TEXT) String parentParam;

  public Mock4Extend(Long _id, Long id, String parentParam) {
    super(_id);
    this.id = id;
    this.parentParam = parentParam;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    Map<String, Object> pack = super.pack();
    pack.put("id", id);
    pack.put("parentParam", "\"" + parentParam + "\"");
    return pack;
  }
}
