package lt.markmerkk.controllers;

import javafx.fxml.FXMLLoader;
import lt.markmerkk.controllers.interfaces.IViewController;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Tests {@link NavigationController#loadController(String)} method
 */
public class NavigationControllerXmlLoadingTest {
    private NavigationController controller;

    //region Helper

    private void init() {
        controller = new NavigationController();
    }

    //endregion

    @Test
    public void shouldInitProperly() throws Exception {
        // Arrange
        init();

        // Act
        // Assert
        assertThat(controller).isNotNull();
    }

    @Test
    public void shouldReturnNullWhenPassingNull() throws Exception {
        // Arrange
        init();

        // Act
        // Assert
        assertThat(controller.loadController(null)).isNull();
    }

    @Test
    public void shouldReturnNullWhenInvalidPath() throws Exception {
        // Arrange
        init();
        controller.loader = mock(FXMLLoader.class);
        doThrow(new IOException("Cant find resource")).when(controller.loader).load();

        // Act
        // Assert
        assertThat(controller.loadController("/InvalidResource.fxml")).isNull();
    }

    @Test
    public void shouldReturnValidWhenPathIsValid() throws Exception {
        // Arrange
        init();
        controller.loader = mock(FXMLLoader.class);
        doReturn(mock(IViewController.class)).when(controller.loader).load();

        // Act
        // Assert
        assertThat(controller.loadController("/SomeValidPath.fxml")).isNotNull();
    }

}