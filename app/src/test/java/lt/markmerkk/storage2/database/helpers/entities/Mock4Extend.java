package lt.markmerkk.storage2.database.helpers.entities;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage2.database.helpers.DBQueryCreateHelperFormQueryTest;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
@Table(name = "mock3") public class Mock4Extend
    extends Mock4Grandparent {
  @Column(value = FieldType.INTEGER) String id;
  @Column(value = FieldType.TEXT) String parent_param;
}
