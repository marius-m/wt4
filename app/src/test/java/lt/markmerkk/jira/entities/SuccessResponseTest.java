package lt.markmerkk.jira.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class SuccessResponseTest {
  @Test public void testSuccessValid() throws Exception {
    // Arrange
    // Act
    // Assert
    SuccessResponse<Object> successResponse = new SuccessResponse<>("object", "success_message");
    assertThat(successResponse.isSuccess()).isTrue();
    assertThat(successResponse.outputMessage()).isNotNull();
    assertThat(successResponse.entity()).isNotNull();
  }

  @Test public void testSuccessNullMessage() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new SuccessResponse<String>(null, "success_message");
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Error response cannot be initialized without a message!");
    }
  }

  @Test public void testSuccessNullEntity() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new SuccessResponse<String>("some_object", null);
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Success response cannot be initialized without an entity!");
    }
  }

}