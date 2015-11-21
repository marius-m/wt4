package lt.markmerkk.storage2.database.helpers;

import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NotInsertable;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

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

  @Test public void testValidPack1() throws Exception {
    // Arrange
    DBQueryInsert insert = new DBQueryInsert();
    // Act
    // Assert
    //assertThat(insert.formQuery(Mock3NotInsertable.class)).isEqualTo();

  }
}