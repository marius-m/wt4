package lt.markmerkk.storage2.jobs;

import java.sql.ResultSet;
import lt.markmerkk.storage2.database.helpers.entities.Mock1Empty;
import lt.markmerkk.storage2.database.helpers.entities.Mock3;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoDefaultConstructor;
import lt.markmerkk.storage2.database.helpers.entities.Mock3NoPacking;
import lt.markmerkk.storage2.database.helpers.entities.Mock4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class QueryJobUnwrapTest {
  @Test public void testNull() throws Exception {
    // Arrange
    QueryJob query = new QueryJob<Mock1Empty>(Mock1Empty.class);
    // Act
    // Assert
    try {
      query.unwrapResult(null);
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("ResultSet is invalid!");
    }
  }

  @Test public void testEmptyClass() throws Exception {
    // Arrange
    QueryJob query = new QueryJob<Mock1Empty>(Mock1Empty.class);
    // Act
    // Assert
    try {
      query.unwrapResult(mock(ResultSet.class));
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided entity is not unpackable!");
    }
  }

  @Test public void testUnpackableClass() throws Exception {
    // Arrange
    QueryJob query = new QueryJob<Mock3NoPacking>(Mock3NoPacking.class);
    // Act
    // Assert
    try {
      query.unwrapResult(mock(ResultSet.class));
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided entity is not unpackable!");
    }
  }

  @Test public void testNoDefaultConstructor() throws Exception {
    // Arrange
    QueryJob query = new QueryJob<Mock3NoDefaultConstructor>(Mock3NoDefaultConstructor.class);
    // Act
    // Assert
    try {
      query.unwrapResult(mock(ResultSet.class));
      fail("Should not unwrap with null input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Provided entity model cant be created!");
    }
  }

  @Test public void testValid() throws Exception {
    // Arrange
    QueryJob<Mock3> query = new QueryJob<Mock3>(Mock3.class);
    ResultSet resultSet = mock(ResultSet.class);
    doReturn(0).when(resultSet).findColumn("title");
    doReturn(1).when(resultSet).findColumn("param");
    doReturn("some_title").when(resultSet).getString(0);
    doReturn("some_param").when(resultSet).getString(1);
    // Act
    query.unwrapResult(resultSet);
    Mock3 mock3 = query.result();
    // Assert
    assertThat(mock3).isNotNull();
    assertThat(mock3.getTitle()).isEqualTo("some_title");
    assertThat(mock3.getParam()).isEqualTo("some_param");
  }
}