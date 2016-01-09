package lt.markmerkk;

import lt.markmerkk.utils.LastUpdateController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

/**
 * Created by mariusmerkevicius on 1/9/16.
 */
public class AutoSync2CurrentSelectionValueTest {

  @Mock
  LastUpdateController updateController;

  private AutoSync2 sync2;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    sync2 = spy(new AutoSync2());
    sync2.lastUpdateController = updateController;
  }

  @Test
  public void currentSelectionValue_inputNull_returnDisabled() throws Exception {
    // Arrange
    sync2.currentSelection = null;

    // Act
    long value = sync2.currentSelectionValue();

    // Assert
    assertThat(value).isEqualTo(-1);
  }

  @Test
  public void currentSelectionValue_inputMalformed_returnDisabled() throws Exception {
    // Arrange
    sync2.currentSelection = "Asdadasf";

    // Act
    long value = sync2.currentSelectionValue();

    // Assert
    assertThat(value).isEqualTo(-1);
  }

  @Test
  public void currentSelectionValue_inputDisabled_returnDisabled() throws Exception {
    // Arrange
    sync2.currentSelection = AutoSync2.REFRESH_NEVER;

    // Act
    long value = sync2.currentSelectionValue();

    // Assert
    assertThat(value).isEqualTo(-1);
  }

  @Test
  public void currentSelectionValue_input15_returnValid() throws Exception {
    // Arrange
    sync2.currentSelection = AutoSync2.REFRESH_15;

    // Act
    long value = sync2.currentSelectionValue();

    // Assert
    assertThat(value).isEqualTo(AutoSync2.TERM_15);
  }

  @Test
  public void currentSelectionValue_input30_returnValid() throws Exception {
    // Arrange
    sync2.currentSelection = AutoSync2.REFRESH_30;

    // Act
    long value = sync2.currentSelectionValue();

    // Assert
    assertThat(value).isEqualTo(AutoSync2.TERM_30);
  }

  @Test
  public void currentSelectionValue_input60_returnValid() throws Exception {
    // Arrange
    sync2.currentSelection = AutoSync2.REFRESH_60;

    // Act
    long value = sync2.currentSelectionValue();

    // Assert
    assertThat(value).isEqualTo(AutoSync2.TERM_60);
  }
}