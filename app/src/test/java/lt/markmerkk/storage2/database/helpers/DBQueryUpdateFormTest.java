package lt.markmerkk.storage2.database.helpers;

import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoIndexable;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NullIndex;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NullPacking;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoInterfaces;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DBQueryUpdateFormTest {
  @Test public void testNull() throws Exception {
    // Arrange
    DBQueryUpdate update = new DBQueryUpdate();
    // Act
    // Assert
    try {
      update.formQuery(null, mock(Mock3.class));
      fail("Should not do an update query");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cant create query for a null value!");
    }
  }

  @Test public void testEmpty() throws Exception {
    // Arrange
    DBQueryUpdate update = new DBQueryUpdate();
    // Act
    // Assert
    try {
      update.formQuery(Mock1Empty.class, null);
      fail("Should not do an update query");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not have @Table annotation!");
    }
  }

  @Test public void testNotPackable() throws Exception {
    // Arrange
    DBQueryUpdate update = new DBQueryUpdate();
    // Act
    // Assert
    try {
      update.formQuery(Mock3NoInterfaces.class, new Mock3NoInterfaces());
      fail("Should not do an update query");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not implement DBPackable!");
    }
  }

  @Test public void testNotIndexable() throws Exception {
    // Arrange
    DBQueryUpdate update = new DBQueryUpdate();
    // Act
    // Assert
    try {
      update.formQuery(Mock3NoIndexable.class, new Mock3NoIndexable());
      fail("Should not do an update query");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not implement DBIndexable!");
    }
  }

  @Test public void testNullPacking() throws Exception {
    // Arrange
    DBQueryUpdate update = new DBQueryUpdate();
    // Act
    Mock3NullPacking mock3 = new Mock3NullPacking("some_title", "some_params");
    // Assert
    try {
      update.formQuery(Mock3NullPacking.class, mock3);
      fail("Cannot form without proper packing function");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form columns without a map!");
    } catch (UnsupportedOperationException e) {
      e.printStackTrace();
    }
  }

  @Test public void testNullIndexClause() throws Exception {
    // Arrange
    DBQueryUpdate update = new DBQueryUpdate();
    // Act
    Mock3NullIndex mock3 = new Mock3NullIndex("some_title", "some_params");
    // Assert
    try {
      update.formQuery(Mock3NullIndex.class, mock3);
      fail("Cannot form without proper packing function");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form query without a index clause!");
    } catch (UnsupportedOperationException e) {
      e.printStackTrace();
    }
  }

  @Test public void testValidPack1() throws Exception {
    // Arrange
    DBQueryUpdate update = new DBQueryUpdate();
    // Act
    Mock3 mock3 = new Mock3("some_title", "some_params");
    // Assert
    //assertEquals("UPDATE mock3 (title,param) VALUES (\"some_title\",\"some_params\");",
    assertEquals(
        "UPDATE mock3 SET (title=\"some_title\",param=\"some_params\") WHERE title = 'some_title'",
        update.formQuery(Mock3.class, mock3));
  }

  @Test public void testValidPack2() throws Exception {
    // Arrange
    DBQueryUpdate update = new DBQueryUpdate();
    // Act
    Mock4 mock = new Mock4(
        20L,
        30L,
        "some_parent_param",
        "some_title",
        "some_name"
    );
    // Assert
    //assertEquals("UPDATE mock3 (title,param) VALUES (\"some_title\",\"some_params\");",
    assertEquals(
        "UPDATE mock3 SET (_id=20,id=30,parentParam=\"some_parent_param\",title=\"some_title\",name=\"some_name\") WHERE _id = '20'",
        update.formQuery(Mock3.class, mock));
  }
}