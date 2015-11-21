package lt.markmerkk.storage2.database.helpers.entities;

import java.util.Map;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock4") public class Mock4 extends Mock4Extend {
  @Column(value = FieldType.TEXT) String title;
  @Column(value = FieldType.TEXT) String name;

  public Mock4(Long _id, Long id, String parentParam, String title, String name) {
    super(_id, id, parentParam);
    this.title = title;
    this.name = name;
  }

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    Map<String, Object> pack = super.pack();
    pack.put("title", "\"" + title + "\"");
    pack.put("name", "\"" + name + "\"");
    return pack;
  }
}
