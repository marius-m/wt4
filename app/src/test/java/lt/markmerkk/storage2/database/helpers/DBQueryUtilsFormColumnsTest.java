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
public class DBQueryUtilsFormColumnsTest {
  @Test public void testNull() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.formColumns(null);
      fail("Should throw an illegal argument exception");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form columns without a class!");
    }
  }

  @Test public void testEmpty() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumns(Mock1.class)).isEqualTo("");
  }

  @Test public void testEmpty2() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumns(Mock2.class)).isEqualTo("");
  }

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumns(Mock3.class)).isEqualTo(" (title TEXT,param INTEGER)");
  }

  @Test public void testValidWithExtend() throws Exception {
    // Arrange
    DBQueryCreate helper = new DBQueryCreate();

    // Act
    // Assert
    assertThat(DBQueryUtils.formColumns(Mock4.class)).isEqualTo(" (title TEXT,param INTEGER,id INTEGER,parent_param TEXT,_id INTEGER)");
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