package lt.markmerkk;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 1/9/16.
 */
public class AutoSync2CurrentSelectionTest {
  private AutoSync2 sync2;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    sync2 = new AutoSync2();
  }

  @Test
  public void currentSelection_inputNull_returnNever() throws Exception {
    // Arrange
    sync2.currentSelection = null;

    // Act
    String selection = sync2.currentSelection();

    // Assert
    assertThat(selection).isEqualTo(AutoSync2.REFRESH_NEVER);
  }

  @Test
  public void currentSelection_inputMalformed_returnNever() throws Exception {
    // Arrange
    sync2.currentSelection = "asdfasdfasd";

    // Act
    String selection = sync2.currentSelection();

    // Assert
    assertThat(selection).isEqualTo(AutoSync2.REFRESH_NEVER);
  }

  @Test
  public void currentSelection_inputNever_returnNever() throws Exception {
    // Arrange
    sync2.currentSelection = AutoSync2.REFRESH_NEVER;

    // Act
    String selection = sync2.currentSelection();

    // Assert
    assertThat(selection).isEqualTo(AutoSync2.REFRESH_NEVER);
  }

  @Test
  public void currentSelection_input15_return15() throws Exception {
    // Arrange
    sync2.currentSelection = AutoSync2.REFRESH_15;

    // Act
    String selection = sync2.currentSelection();

    // Assert
    assertThat(selection).isEqualTo(AutoSync2.REFRESH_15);
  }

  @Test
  public void currentSelection_input30_return30() throws Exception {
    // Arrange
    sync2.currentSelection = AutoSync2.REFRESH_30;

    // Act
    String selection = sync2.currentSelection();

    // Assert
    assertThat(selection).isEqualTo(AutoSync2.REFRESH_30);
  }

  @Test
  public void currentSelection_input60_return60() throws Exception {
    // Arrange
    sync2.currentSelection = AutoSync2.REFRESH_60;

    // Act
    String selection = sync2.currentSelection();

    // Assert
    assertThat(selection).isEqualTo(AutoSync2.REFRESH_60);
  }
}