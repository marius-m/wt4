package lt.markmerkk.utils;

import lt.markmerkk.mvp.UserSettings;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 1/5/16.
 */
public class LastUpdateControllerImplInitTest {
  @Test
  public void init_storageOutputValid_valid() throws Exception {
    // Arrange
    LastUpdateControllerImpl controller = new LastUpdateControllerImpl(mock(UserSettings.class));
    doReturn("12345").when(controller.settings).getCustom(eq(LastUpdateControllerImpl.LAST_UPDATE));

    // Act
    controller.init();

    // Assert
    assertThat(controller.lastUpdate).isEqualTo(12345);
  }

  @Test
  public void init_storageOutputNull_setDefaultValue() throws Exception {
    // Arrange
    LastUpdateControllerImpl controller = new LastUpdateControllerImpl(mock(UserSettings.class));
    doReturn(null).when(controller.settings).getCustom(eq(LastUpdateControllerImpl.LAST_UPDATE));

    // Act
    controller.init();

    // Assert
    assertThat(controller.lastUpdate).isEqualTo(0);
  }

  @Test
  public void init_storageOutputEmpty_setDefaultValue() throws Exception {
    // Arrange
    LastUpdateControllerImpl controller = new LastUpdateControllerImpl(mock(UserSettings.class));
    doReturn("").when(controller.settings).getCustom(eq(LastUpdateControllerImpl.LAST_UPDATE));

    // Act
    controller.init();

    // Assert
    assertThat(controller.lastUpdate).isEqualTo(0);
  }

  @Test
  public void init_storageOutputMalformed_setDefaultValue() throws Exception {
    // Arrange
    LastUpdateControllerImpl controller = new LastUpdateControllerImpl(mock(UserSettings.class));
    doReturn("asdf").when(controller.settings).getCustom(eq(LastUpdateControllerImpl.LAST_UPDATE));

    // Act
    controller.init();

    // Assert
    assertThat(controller.lastUpdate).isEqualTo(0);
  }
}