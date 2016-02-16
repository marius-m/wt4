package lt.markmerkk.storage2;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class SimpleLogBuilderParseUriTest {
  @Test public void testValid() throws Exception {
    // Arrange

    // Act
    long id = SimpleLogBuilder.parseUri("https://jira.ito.lt/rest/api/2/issue/31463/worklog/73051");

    // Assert
    assertThat(id).isEqualTo(73051);
  }

  @Test public void testInputNull() throws Exception {
    // Arrange

    // Act
    long id = SimpleLogBuilder.parseUri(null);

    // Assert
    assertThat(id).isEqualTo(0);
  }

  @Test public void testInputEmpty() throws Exception {
    // Arrange

    // Act
    long id = SimpleLogBuilder.parseUri("");

    // Assert
    assertThat(id).isEqualTo(0);
  }

  @Test public void testInputMalformed() throws Exception {
    // Arrange

    // Act
    long id = SimpleLogBuilder.parseUri("asdf");

    // Assert
    assertThat(id).isEqualTo(0);
  }
}