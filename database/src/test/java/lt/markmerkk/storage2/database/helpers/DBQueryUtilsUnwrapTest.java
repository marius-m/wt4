package lt.markmerkk.storage2.database.helpers;

import java.sql.ResultSet;
import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoDefaultConstructor;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoInterfaces;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import lt.markmerkk.storage2.jobs.QueryJob;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class DBQueryUtilsUnwrapTest {
  @Test public void testResult() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.unwrapResult(Mock3.class, null);
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("ResultSet is invalid!");
    }
  }
  @Test public void testClass() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.unwrapResult(null, mock(ResultSet.class));
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Class is invalid!");
    }
  }

  @Test public void testEmptyClass() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.unwrapResult(Mock1Empty.class, mock(ResultSet.class));
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided entity is not unpackable!");
    }
  }

  @Test public void testUnpackableClass() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.unwrapResult(Mock3NoInterfaces.class, mock(ResultSet.class));
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided entity is not unpackable!");
    }
  }

  @Test public void testNoDefaultConstructor() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.unwrapResult(Mock3NoDefaultConstructor.class, mock(ResultSet.class));
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided entity model cant be created!");
    }
  }

  @Test public void testValid() throws Exception {
    // Arrange
    ResultSet resultSet = mock(ResultSet.class);
    doReturn(0).when(resultSet).findColumn("title");
    doReturn(1).when(resultSet).findColumn("param");
    doReturn("some_title").when(resultSet).getString(0);
    doReturn("some_param").when(resultSet).getString(1);
    // Act
    Mock3 mock3 = DBQueryUtils.unwrapResult(Mock3.class, resultSet);
    // Assert
    assertThat(mock3).isNotNull();
    assertThat(mock3.getTitle()).isEqualTo("some_title");
    assertThat(mock3.getParam()).isEqualTo("some_param");
  }

  @Test public void testValid2() throws Exception {
    // Arrange
    ResultSet resultSet = mock(ResultSet.class);
    doReturn(0).when(resultSet).findColumn("_id");
    doReturn(1).when(resultSet).findColumn("id");
    doReturn(2).when(resultSet).findColumn("parentParam");
    doReturn(3).when(resultSet).findColumn("title");
    doReturn(4).when(resultSet).findColumn("name");
    doReturn(20L).when(resultSet).getLong(0);
    doReturn(30L).when(resultSet).getLong(1);
    doReturn("some_parent_param").when(resultSet).getString(2);
    doReturn("some_title").when(resultSet).getString(3);
    doReturn("some_name").when(resultSet).getString(4);
    // Act
    Mock4 mock4 = DBQueryUtils.unwrapResult(Mock4.class, resultSet);
    // Assert
    assertThat(mock4).isNotNull();
    assertThat(mock4.getTitle()).isEqualTo("some_title");
    assertThat(mock4.getName()).isEqualTo("some_name");
    assertThat(mock4.getParentParam()).isEqualTo("some_parent_param");
    assertThat(mock4.get_id()).isEqualTo(20L);
    assertThat(mock4.getId()).isEqualTo(30L);
  }
}