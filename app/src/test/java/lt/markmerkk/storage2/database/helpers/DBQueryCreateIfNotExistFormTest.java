package lt.markmerkk.storage2.database.helpers;

import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock2NoColumns;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class DBQueryCreateIfNotExistFormTest {
  @Test public void testNull() throws Exception {
    // Arrange
    DBQueryCreateIfNotExist helper = new DBQueryCreateIfNotExist();

    // Act
    // Assert
    try {
      helper.formQuery(null);
      fail("Should not form a query for null value!");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cant create query for a null value!");
    }
  }

  @Test public void testEmptyClass() throws Exception {
    // Arrange
    DBQueryCreateIfNotExist helper = new DBQueryCreateIfNotExist();

    // Act
    // Assert
    try {
      helper.formQuery(Mock1Empty.class);
      fail("Should not create a query for an empty class");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not have @Table annotation!");
    }
  }

  @Test public void testValid1() throws Exception {
    // Arrange
    DBQueryCreateIfNotExist helper = new DBQueryCreateIfNotExist();

    // Act
    String queryString = helper.formQuery(Mock2NoColumns.class);

    // Assert
    assertThat(queryString).isNotNull();
    assertThat(queryString).isEqualTo("CREATE TABLE IF NOT EXISTS mock2");
  }

  @Test public void testValid2() throws Exception {
    // Arrange
    DBQueryCreateIfNotExist helper = new DBQueryCreateIfNotExist();

    // Act
    String queryString = helper.formQuery(Mock3.class);

    // Assert
    assertThat(queryString).isNotNull();
    assertThat(queryString).isEqualTo("CREATE TABLE IF NOT EXISTS mock3 (title TEXT,param INTEGER)");
  }

  @Test public void testValid3() throws Exception {
    // Arrange
    DBQueryCreateIfNotExist helper = new DBQueryCreateIfNotExist();

    // Act
    String queryString = helper.formQuery(Mock4.class);

    // Assert
    assertThat(queryString).isNotNull();
    assertThat(queryString).isEqualTo("CREATE TABLE IF NOT EXISTS mock4 (title TEXT,name TEXT,id INTEGER,parentParam TEXT,_id INTEGER)");
  }

}