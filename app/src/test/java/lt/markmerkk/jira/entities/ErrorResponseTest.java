package lt.markmerkk.jira.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class ErrorResponseTest {

  @Test public void testErrorValid() throws Exception {
    // Arrange
    // Act
    // Assert
    ErrorResponse errorResponse = new ErrorResponse("some_error_message");
    assertThat(errorResponse.isSuccess()).isFalse();
    assertThat(errorResponse.outputMessage()).isNotNull();
    assertThat(errorResponse.entity()).isNull();
  }

  @Test public void testErrorNullMessage() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new ErrorResponse(null);
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Error response cannot be initialized without a message!");
    }
  }
}