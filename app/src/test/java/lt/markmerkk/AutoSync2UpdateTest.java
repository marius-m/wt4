package lt.markmerkk;

import lt.markmerkk.utils.LastUpdateController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 1/8/16.
 */
public class AutoSync2UpdateTest {

  @Mock LastUpdateController updateController;

  private AutoSync2 sync2;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    sync2 = spy(new AutoSync2());
    sync2.lastUpdateController = updateController;
  }

  @Test
  public void update_curSelNeverAndDurationLow_neverCallback() throws Exception {
    // Arrange
    doReturn(AutoSync2.TERM_NEVER).when(sync2).currentSelectionValue();
    doReturn(AutoSync2.TERM_MINUTE).when(updateController).durationTillLastUpdate();

    // Act
    boolean isSyncNeeded = sync2.isSyncNeeded();

    // Assert
    assertThat(isSyncNeeded).isFalse();
  }

  @Test
  public void update_curSelNeverAndDurationVeryHigh_neverCallback() throws Exception {
    // Arrange
    doReturn(AutoSync2.TERM_NEVER).when(sync2).currentSelectionValue();
    doReturn(AutoSync2.TERM_MINUTE * 70).when(updateController).durationTillLastUpdate();

    // Act
    boolean isSyncNeeded = sync2.isSyncNeeded();

    // Assert
    assertThat(isSyncNeeded).isFalse();
  }

  @Test
  public void update_curSel15AndDurationLow_neverCallback() throws Exception {
    // Arrange
    doReturn(AutoSync2.TERM_15).when(sync2).currentSelectionValue();
    doReturn(AutoSync2.TERM_MINUTE).when(updateController).durationTillLastUpdate();

    // Act
    boolean isSyncNeeded = sync2.isSyncNeeded();

    // Assert
    assertThat(isSyncNeeded).isFalse();
  }

  @Test
  public void update_curSel15AndDurationHigh_shouldCallback() throws Exception {
    // Arrange
    doReturn(AutoSync2.TERM_15).when(sync2).currentSelectionValue();
    doReturn(AutoSync2.TERM_MINUTE * 70).when(updateController).durationTillLastUpdate();

    // Act
    boolean isSyncNeeded = sync2.isSyncNeeded();

    // Assert
    assertThat(isSyncNeeded).isTrue();
  }

  @Test
  public void update_curSel30AndDurationLow_neverCallback() throws Exception {
    // Arrange
    doReturn(AutoSync2.TERM_30).when(sync2).currentSelectionValue();
    doReturn(AutoSync2.TERM_MINUTE).when(updateController).durationTillLastUpdate();

    // Act
    boolean isSyncNeeded = sync2.isSyncNeeded();

    // Assert
    assertThat(isSyncNeeded).isFalse();
  }

  @Test
  public void update_curSel30AndDurationHigh_shouldCallback() throws Exception {
    // Arrange
    doReturn(AutoSync2.TERM_30).when(sync2).currentSelectionValue();
    doReturn(AutoSync2.TERM_MINUTE * 70).when(updateController).durationTillLastUpdate();

    // Act
    boolean isSyncNeeded = sync2.isSyncNeeded();

    // Assert
    assertThat(isSyncNeeded).isTrue();
  }

  @Test
  public void update_curSel60AndDurationLow_neverCallback() throws Exception {
    // Arrange
    doReturn(AutoSync2.TERM_60).when(sync2).currentSelectionValue();
    doReturn(AutoSync2.TERM_MINUTE).when(updateController).durationTillLastUpdate();

    // Act
    boolean isSyncNeeded = sync2.isSyncNeeded();

    // Assert
    assertThat(isSyncNeeded).isFalse();
  }

  @Test
  public void update_curSel60AndDurationHigh_shouldCallback() throws Exception {
    // Arrange
    doReturn(AutoSync2.TERM_60).when(sync2).currentSelectionValue();
    doReturn(AutoSync2.TERM_MINUTE * 70).when(updateController).durationTillLastUpdate();

    // Act
    boolean isSyncNeeded = sync2.isSyncNeeded();

    // Assert
    assertThat(isSyncNeeded).isTrue();
  }
}