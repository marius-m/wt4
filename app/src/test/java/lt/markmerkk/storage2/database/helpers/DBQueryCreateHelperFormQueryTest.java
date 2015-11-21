package lt.markmerkk.storage2.database.helpers;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class DBQueryCreateHelperFormQueryTest {
  @Test public void testNull() throws Exception {
    // Arrange
    DBQueryCreate helper = new DBQueryCreate();

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
    DBQueryCreate helper = new DBQueryCreate();

    // Act
    // Assert
    try {
      helper.formQuery(Mock1.class);
      fail("Should not create a query for an empty class");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided class does not have @Table annotation!");
    }
  }

  @Test public void testValid1() throws Exception {
    // Arrange
    DBQueryCreate helper = new DBQueryCreate();

    // Act
    String queryString = helper.formQuery(Mock2.class);

    // Assert
    assertThat(queryString).isNotNull();
    assertThat(queryString).isEqualTo("CREATE TABLE mock2");
  }

  @Test public void testValid2() throws Exception {
    // Arrange
    DBQueryCreate helper = new DBQueryCreate();

    // Act
    String queryString = helper.formQuery(Mock3.class);

    // Assert
    assertThat(queryString).isNotNull();
    assertThat(queryString).isEqualTo("CREATE TABLE mock3 (title TEXT,param INTEGER)");
  }

  @Test public void testValid3() throws Exception {
    // Arrange
    DBQueryCreate helper = new DBQueryCreate();

    // Act
    String queryString = helper.formQuery(Mock4.class);

    // Assert
    assertThat(queryString).isNotNull();
    assertThat(queryString).isEqualTo("CREATE TABLE mock3 (title TEXT,param INTEGER,id INTEGER,parent_param TEXT,_id INTEGER)");
  }

  //region Classes

  private class Mock1 { }

  @Table(name = "mock2")
  private class Mock2 {
  }

  @Table(name = "mock3")
  private class Mock3 {
    @Column(value = FieldType.TEXT)
    String title;
    @Column(value = FieldType.INTEGER)
    String param;
  }

  @Table(name = "mock3")
  private class Mock4 extends Mock4Extend {
    @Column(value = FieldType.TEXT)
    String title;
    @Column(value = FieldType.INTEGER)
    String param;
  }

  @Table(name = "mock3")
  private class Mock4Extend extends Mock4Grandparent {
    @Column(value = FieldType.INTEGER)
    String id;
    @Column(value = FieldType.TEXT)
    String parent_param;
  }

  @Table(name = "mock3")
  private class Mock4Grandparent {
    @Column(value = FieldType.INTEGER)
    String _id;
  }

  //endregion

}