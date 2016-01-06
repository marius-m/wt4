package lt.markmerkk.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 1/5/16.
 */
public class LastUpdateControllerInitTest {
  @Test
  public void init_storageOutputValid_valid() throws Exception {
    // Arrange
    LastUpdateController controller = new LastUpdateController();
    controller.settings = mock(UserSettings.class);
    doReturn("12345").when(controller.settings).getCustom(eq(LastUpdateController.LAST_UPDATE));

    // Act
    controller.init();

    // Assert
    assertThat(controller.lastUpdate).isEqualTo(12345);
  }

  @Test
  public void init_storageOutputNull_setDefaultValue() throws Exception {
    // Arrange
    LastUpdateController controller = new LastUpdateController();
    controller.settings = mock(UserSettings.class);
    doReturn(null).when(controller.settings).getCustom(eq(LastUpdateController.LAST_UPDATE));

    // Act
    controller.init();

    // Assert
    assertThat(controller.lastUpdate).isEqualTo(0);
  }

  @Test
  public void init_storageOutputEmpty_setDefaultValue() throws Exception {
    // Arrange
    LastUpdateController controller = new LastUpdateController();
    controller.settings = mock(UserSettings.class);
    doReturn("").when(controller.settings).getCustom(eq(LastUpdateController.LAST_UPDATE));

    // Act
    controller.init();

    // Assert
    assertThat(controller.lastUpdate).isEqualTo(0);
  }

  @Test
  public void init_storageOutputMalformed_setDefaultValue() throws Exception {
    // Arrange
    LastUpdateController controller = new LastUpdateController();
    controller.settings = mock(UserSettings.class);
    doReturn("asdf").when(controller.settings).getCustom(eq(LastUpdateController.LAST_UPDATE));

    // Act
    controller.init();

    // Assert
    assertThat(controller.lastUpdate).isEqualTo(0);
  }
}