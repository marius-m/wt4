package lt.markmerkk.storage2.database.helpers;

import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock2NoColumns;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoExtend;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
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
    assertThat(DBQueryUtils.formColumns(Mock1Empty.class)).isEqualTo("");
  }

  @Test public void testEmpty2() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumns(Mock2NoColumns.class)).isEqualTo("");
  }

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumns(Mock3NoExtend.class)).isEqualTo(" (title TEXT,param INTEGER)");
  }

  @Test public void testValidWithExtend() throws Exception {
    // Arrange
    DBQueryCreate helper = new DBQueryCreate();

    // Act
    // Assert
    assertThat(DBQueryUtils.formColumns(Mock4.class)).isEqualTo(" (title TEXT,param INTEGER,id INTEGER,parent_param TEXT,_id INTEGER)");
  }

}