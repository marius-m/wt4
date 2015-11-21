package lt.markmerkk.storage2.database.helpers;

import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoExtend;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoExtendNoPacking;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NotInsertable;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class DBQueryInsertTest {
  @Test public void testNull() throws Exception {
    // Arrange
    DBQueryInsert insert = new DBQueryInsert();
    // Act
    // Assert
    try {
      insert.formQuery(null, null);
      fail("Should not do an insert query");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cant create query for a null value!");
    }
  }

  @Test public void testEmpty() throws Exception {
    // Arrange
    DBQueryInsert insert = new DBQueryInsert();
    // Act
    // Assert
    try {
      insert.formQuery(Mock1Empty.class, null);
      fail("Should not do an insert query");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not have @Table annotation!");
    }
  }

  @Test public void testNotInstertable() throws Exception {
    // Arrange
    DBQueryInsert insert = new DBQueryInsert();
    // Act
    // Assert
    try {
      insert.formQuery(Mock3NotInsertable.class, null);
      fail("Should not do an insert query");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not implement DBInsertable!");
    }
  }

  @Test public void testNullPacking() throws Exception {
    // Arrange
    DBQueryInsert insert = new DBQueryInsert();
    // Act
    Mock3NoExtendNoPacking mock3 = new Mock3NoExtendNoPacking("some_title", "some_params");
    // Assert
    try {
      insert.formQuery(Mock3NoExtendNoPacking.class, mock3);
      fail("Cannot form without proper packing function");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form columns without a map!");
    } catch (UnsupportedOperationException e) {
      e.printStackTrace();
    }
  }

  @Test public void testValidPack1() throws Exception {
    // Arrange
    DBQueryInsert insert = new DBQueryInsert();
    // Act
    Mock3NoExtend mock3 = new Mock3NoExtend("some_title", "some_params");
    // Assert
    assertEquals("INSERT INTO mock3 (title,param) VALUES (\"some_title\",\"some_params\");",
        insert.formQuery(Mock3NoExtend.class, mock3));

  }

  @Test public void testValidPack2() throws Exception {
    // Arrange
    DBQueryInsert insert = new DBQueryInsert();
    // Act
    Mock4 mock4 = new Mock4(20L, 30L, "some_param", "some_title", "some_name");
    // Assert
    assertEquals(
        "INSERT INTO mock4 (_id,id,parentParam,title,name) VALUES (20,30,\"some_param\",\"some_title\",\"some_name\");",
        insert.formQuery(Mock4.class, mock4));
  }

}