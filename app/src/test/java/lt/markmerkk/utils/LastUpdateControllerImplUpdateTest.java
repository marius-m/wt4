package lt.markmerkk.utils;

import lt.markmerkk.mvp.UserSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 1/5/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class LastUpdateControllerImplUpdateTest {

  @Spy
  private LastUpdateControllerImpl controller = new LastUpdateControllerImpl(mock(UserSettings.class));

  @Before
  public void setUp() {
    doReturn("0").when(controller.settings).getCustom(eq(LastUpdateControllerImpl.LAST_UPDATE));
    controller.init();
  }

  @Test
  public void output_withSameUpdate_returnZero() throws Exception {
    // Arrange
    controller.lastUpdate = 1000;
    doReturn(1000L).when(controller).now();

    // Act
    // Assert
    assertThat(controller.getOutput()).isEqualTo("0m");
  }

  @Test
  public void output_withLastUpdateHigher_returnZero() throws Exception {
    // Arrange
    controller.lastUpdate = 10000;
    doReturn(1000L).when(controller).now();

    // Act
    // Assert
    assertThat(controller.getOutput()).isEqualTo("0m");
  }

  @Test
  public void output_withLastUpdateHigher_returnValid() throws Exception {
    // Arrange
    controller.lastUpdate = 1000;
    doReturn(1000000L).when(controller).now();

    // Act
    // Assert
    assertThat(controller.getOutput()).isEqualTo("16m");
  }

  @Test
  public void output_withZereLastUpdate_returnNever() throws Exception {
    // Arrange
    controller.lastUpdate = 0;
    doReturn(1000000L).when(controller).now();

    // Act
    // Assert
    assertThat(controller.getOutput()).isEqualTo("Never");
  }

  @Test
  public void output_validWithLoadingFlag_returnLoading() throws Exception {
    // Arrange
    controller.lastUpdate = 1000;
    doReturn(1000000L).when(controller).now();
    controller.setLoading(true);

    // Act
    // Assert
    assertThat(controller.getOutput()).isEqualTo("Loading...");
  }

  @Test
  public void output_validWithErrorFlag_returnLoading() throws Exception {
    // Arrange
    controller.lastUpdate = 1000;
    doReturn(1000000L).when(controller).now();
    controller.setError("Some error!");

    // Act
    // Assert
    assertThat(controller.getOutput()).isEqualTo("Error. Some error!");
  }

  @Test
  public void output_validWithLoadingWithErrorFlag_returnLoading() throws Exception {
    // Arrange
    controller.lastUpdate = 1000;
    doReturn(1000000L).when(controller).now();
    controller.setLoading(true);
    controller.setError("Some error!");

    // Act
    // Assert
    assertThat(controller.getOutput()).isEqualTo("Loading...");
  }

}