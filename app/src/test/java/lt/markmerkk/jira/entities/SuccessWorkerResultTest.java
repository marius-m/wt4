package lt.markmerkk.jira.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class SuccessWorkerResultTest {
  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    SuccessWorkerResult<Object>
        successResponse = new SuccessWorkerResult<>("valid_tag", "success_message");
    assertThat(successResponse.isSuccess()).isTrue();
    assertThat(successResponse.entity()).isNotNull();
    assertThat(successResponse.tag()).isNotNull();
  }

  @Test public void testNullEntity() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      new SuccessWorkerResult<String>("valid_tag", null);
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
      new SuccessWorkerResult<String>(null, "valid entity");
      fail("Should not create with invalid input");
    } catch (Exception e) {
      assertThat(e).hasMessage("Response cannot be initialized without a tag!");
    }
  }

}