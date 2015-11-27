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
    ErrorWorkerResult errorResponse = new ErrorWorkerResult("some_tag", "some_error_message");
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
      new ErrorWorkerResult("some_tag", null);
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Response cannot be initialized without a message!");
    }
  }

  @Test public void testNullTag() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new ErrorWorkerResult(null, "output_message");
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Response cannot be initialized without a tag!");
    }
  }
}