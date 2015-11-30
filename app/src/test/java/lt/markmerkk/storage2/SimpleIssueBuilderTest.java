package lt.markmerkk.storage2;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by mariusmerkevicius on 11/30/15.
 */
public class SimpleIssueBuilderTest {
  @Test public void testEmpty() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      SimpleIssue issue = new SimpleIssueBuilder().build();
      fail("Should not build issue");
    } catch (Exception e) {
      assertThat(e).hasMessage("Project must be provided!");
    }
  }

  @Test public void testMissingProject() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      SimpleIssue issue = new SimpleIssueBuilder()
          .setKey("valid_key")
          .setDescription("valid_description")
          .build();
      fail("Should not build issue");
    } catch (Exception e) {
      assertThat(e).hasMessage("Project must be provided!");
    }
  }

  @Test public void testMissingKey() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      SimpleIssue issue = new SimpleIssueBuilder().setProject("valid_project")
          .setDescription("valid_description")
          .build();
      fail("Should not build issue");
    } catch (Exception e) {
      assertThat(e).hasMessage("Key must be provided!");
    }
  }

  @Test public void testValidMissingDescription() throws Exception {
    // Arrange
    SimpleIssue issue =
        new SimpleIssueBuilder().setProject("valid_project").setKey("valid_key").build();
    // Act
    // Assert
    assertThat(issue).isNotNull();
    assertThat(issue.project).isEqualTo("valid_project");
    assertThat(issue.key).isEqualTo("valid_key");
    assertThat(issue.description).isNull();
  }

  @Test public void testValid() throws Exception {
    // Arrange
    SimpleIssue issue =
        new SimpleIssueBuilder()
            .setProject("valid_project").setKey("valid_key").setDescription("valid_description")
            .build();
    // Act
    // Assert
    assertThat(issue).isNotNull();
    assertThat(issue.project).isEqualTo("valid_project");
    assertThat(issue.key).isEqualTo("valid_key");
    assertThat(issue.description).isEqualTo("valid_description");
  }
}