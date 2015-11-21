package lt.markmerkk.storage2.database.helpers;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Testing {@link DBQueryUtils#formColumnsFromMapKeys(Map)} and
 * {@link DBQueryUtils#formColumnsFromMapValues(Map)}
 * as those functions are related to each other
 */
public class DBQueryUtilsColumnsFromMapTest {
  //region Init test
  @Test public void testNullMap() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.formColumnsFromMapKeys(null);
      fail("Should not form with invalid input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form columns without a map!");
    }
  }

  @Test public void testEmptyMap() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.formColumnsFromMapKeys(new HashMap<>());
      fail("Should not form with invalid input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form columns with an empty map!");
    }
  }

  @Test public void testNullMapValues() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.formColumnsFromMapValues(null);
      fail("Should not form with invalid input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form columns without a map!");
    }
  }

  @Test public void testEmptyMapValues() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      DBQueryUtils.formColumnsFromMapValues(new HashMap<>());
      fail("Should not form with invalid input");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Cannot form columns with an empty map!");
    }
  }
  //endregion

  @Test public void testValid1() throws Exception {
    // Arrange
    HashMap<String, String> map = new HashMap<String, String>() {{
      put("key1", "value1");
      put("key2", "value2");
      put("key3", "value3");
    }};
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromMapKeys(map)).isEqualTo("(key1,key2,key3)");
    assertThat(DBQueryUtils.formColumnsFromMapValues(map)).isEqualTo("(value1,value2,value3)");
  }

  @Test public void testMalformValid1() throws Exception {
    // Arrange
    HashMap<String, String> map = new HashMap<String, String>() {{
      put("key1", "value1");
      put(null, "value2");
      put("key3", "value3");
    }};
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromMapKeys(map)).isEqualTo("(key1,key3)");
    assertThat(DBQueryUtils.formColumnsFromMapValues(map)).isEqualTo("(value1,value3)");
  }

  @Test public void testMalformValid2() throws Exception {
    // Arrange
    HashMap<String, String> map = new HashMap<String, String>() {{
      put("key1", "value1");
      put("", "value2");
      put("key3", "value3");
    }};
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromMapKeys(map)).isEqualTo("(key1,key3)");
    assertThat(DBQueryUtils.formColumnsFromMapValues(map)).isEqualTo("(value1,value3)");
  }

  @Test public void testMalformValid3() throws Exception {
    // Arrange
    HashMap<String, String> map = new HashMap<String, String>() {{
      put("key1", "value1");
      put("key2", null);
      put("key3", "value3");
    }};
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromMapKeys(map)).isEqualTo("(key1,key3)");
    assertThat(DBQueryUtils.formColumnsFromMapValues(map)).isEqualTo("(value1,value3)");
  }

  @Test public void testMalformValid4() throws Exception {
    // Arrange
    HashMap<String, String> map = new HashMap<String, String>() {{
      put("key1", "value1");
      put("key2", null);
      put("key3", "value3");
    }};
    // Act
    // Assert
    assertThat(DBQueryUtils.formColumnsFromMapKeys(map)).isEqualTo("(key1,key3)");
    assertThat(DBQueryUtils.formColumnsFromMapValues(map)).isEqualTo("(value1,value3)");
  }
}