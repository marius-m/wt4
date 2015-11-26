package lt.markmerkk.jira.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class JiraResponseInitTest {
  @Test public void testSuccessValid() throws Exception {
    // Arrange
    // Act
    // Assert
    JiraResponse<Object> successResponse = new JiraResponse<>("object", "success_message");
    assertThat(successResponse.isSuccess()).isTrue();
    assertThat(successResponse.outputMessage()).isNotNull();
    assertThat(successResponse.entity()).isNotNull();
  }

  @Test public void testErrorValid() throws Exception {
    // Arrange
    // Act
    // Assert
    JiraResponse<Object> errorResponse = new JiraResponse<>("some_error_message");
    assertThat(errorResponse.isSuccess()).isFalse();
    assertThat(errorResponse.outputMessage()).isNotNull();
    assertThat(errorResponse.entity()).isNull();
  }

  @Test public void testSuccessNullMessage() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new JiraResponse<Object>(null, "success_message");
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
      new JiraResponse<Object>("some_object", null);
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Success response cannot be initialized without an entity!");
    }
  }

  @Test public void testErrorNullMessage() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new JiraResponse<Object>(null, "success_message");
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Error response cannot be initialized without a message!");
    }
  }
}