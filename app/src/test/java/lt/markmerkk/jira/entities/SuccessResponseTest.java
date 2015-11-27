package lt.markmerkk.jira.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class SuccessResponseTest {
  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    SuccessWorkerResult<Object>
        successResponse = new SuccessWorkerResult<>("valid_tag", "object", "success_message");
    assertThat(successResponse.isSuccess()).isTrue();
    assertThat(successResponse.outputMessage()).isNotNull();
    assertThat(successResponse.entity()).isNotNull();
    assertThat(successResponse.tag()).isNotNull();
  }

  @Test public void testNullMessage() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new SuccessWorkerResult<String>("valid_tag", null, "success_message");
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Response cannot be initialized without a message!");
    }
  }

  @Test public void testNullEntity() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new SuccessWorkerResult<String>("valid_tag", "some_object", null);
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Response cannot be initialized without an entity!");
    }
  }

  @Test public void testNullTag() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new SuccessWorkerResult<String>(null, "some_object", "valid entity");
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Response cannot be initialized without a tag!");
    }
  }

}