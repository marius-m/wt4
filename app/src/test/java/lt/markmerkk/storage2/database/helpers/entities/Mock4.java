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
  @Column(value = FieldType.INTEGER) String param;

  public Mock4(String _id, String id, String parent_param, String title, String param) {
    super(_id, id, parent_param);
    this.title = title;
    this.param = param;
  }

  @Override public Map<String, String> pack() throws IllegalArgumentException {
    Map<String, String> pack = super.pack();
    pack.put("title", "\"" + title + "\"");
    pack.put("param", parent_param);
    return pack;
  }
}
