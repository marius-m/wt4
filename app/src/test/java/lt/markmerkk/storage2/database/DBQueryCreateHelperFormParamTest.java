package lt.markmerkk.storage2.database;

import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/21/15.
 */
public class DBQueryCreateHelperFormParamTest {
  @Test public void testNull() throws Exception {
    // Arrange
    DBQueryCreateHelper helper = new DBQueryCreateHelper();

    // Act
    // Assert
    assertThat(helper.formColumns(null)).isEqualTo("");
  }

  @Test public void testEmpty() throws Exception {
    // Arrange
    DBQueryCreateHelper helper = new DBQueryCreateHelper();

    // Act
    // Assert
    assertThat(helper.formColumns(Mock1.class)).isEqualTo("");
  }

  @Test public void testEmpty2() throws Exception {
    // Arrange
    DBQueryCreateHelper helper = new DBQueryCreateHelper();

    // Act
    // Assert
    assertThat(helper.formColumns(Mock2.class)).isEqualTo("");
  }

  @Test public void testValid() throws Exception {
    // Arrange
    DBQueryCreateHelper helper = new DBQueryCreateHelper();

    // Act
    // Assert
    assertThat(helper.formColumns(Mock3.class)).isEqualTo(" (title TEXT,param INTEGER)");
  }

  @Test public void testValidWithExtend() throws Exception {
    // Arrange
    DBQueryCreateHelper helper = new DBQueryCreateHelper();

    // Act
    // Assert
    assertThat(helper.formColumns(Mock4.class)).isEqualTo(" (title TEXT,param INTEGER,id INTEGER,parent_param TEXT,_id INTEGER)");
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