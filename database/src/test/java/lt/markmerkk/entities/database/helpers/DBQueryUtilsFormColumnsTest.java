package lt.markmerkk.entities.database.helpers;

import lt.markmerkk.entities.database.helpers.entities.Mock1Empty;
import lt.markmerkk.entities.database.helpers.entities.Mock2NoColumns;
import lt.markmerkk.entities.database.helpers.entities.Mock3;
import lt.markmerkk.entities.database.helpers.entities.Mock4;
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
      DBQueryUtils.formColumnsFromClass(null);
      fail("Should throw an illegal argument exception");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form columns without a class!");
    }
  }

  @Test public void testEmpty() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromClass(Mock1Empty.class)).isEqualTo("");
  }

  @Test public void testEmpty2() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromClass(Mock2NoColumns.class)).isEqualTo("");
  }

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromClass(Mock3.class)).isEqualTo(" (title TEXT,param INTEGER)");
  }

  @Test public void testValidWithExtend() throws Exception {
    // Arrange
    DBQueryCreate helper = new DBQueryCreate();

    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromClass(Mock4.class)).isEqualTo(" (title TEXT,name TEXT,id INTEGER,parentParam TEXT,_id INTEGER)");
  }

}