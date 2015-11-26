package lt.markmerkk.jira.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class ErrorResponseTest {

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    ErrorResponse errorResponse = new ErrorResponse("some_tag", "some_error_message");
    assertThat(errorResponse.isSuccess()).isFalse();
    assertThat(errorResponse.outputMessage()).isNotNull();
    assertThat(errorResponse.entity()).isNull();
    assertThat(errorResponse.tag()).isNotNull();
  }

  @Test public void testNullMessage() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new ErrorResponse("some_tag", null);
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Error response cannot be initialized without a message!");
    }
  }

  @Test public void testNullTag() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new ErrorResponse(null, "output_message");
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Error response cannot be initialized without a tag!");
    }
  }
}