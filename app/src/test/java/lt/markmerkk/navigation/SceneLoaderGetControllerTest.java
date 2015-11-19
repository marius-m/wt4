package lt.markmerkk.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lt.markmerkk.navigation.interfaces.IViewController;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Tests {@link SceneLoader#getController(Parent)} method
 */
public class SceneLoaderGetControllerTest {
    private SceneLoader loader;

    //region Helper

    private void init() {
        loader = new SceneLoader();
        loader.loader = mock(FXMLLoader.class);
    }

    //endregion

    //region Classes

    private class SomeController {}

    //endregion

    @Test
    public void shouldBreakIfNoParentIsSpecified() throws Exception {
        // Arrange
        init();
        // Act

        try {
            loader.getController(null);
            fail("Should not get controller if not parent is specified");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("No parent is specified!");
        }
        // Assert
    }

    @Test
    public void shouldReturnValidWhenParentSpecified() throws Exception {
        // Arrange
        init();
        doReturn(mock(IViewController.class)).when(loader.loader).getController();

        // Act
        // Assert
        assertThat(loader.getController(mock(Parent.class))).isNotNull();
    }

    @Test
    public void shouldReturnNullWhenReturningDifferentController() throws Exception {
        // Arrange
        init();
        doReturn(mock(SomeController.class)).when(loader.loader).getController();

        // Act
        // Assert
        try {
            loader.getController(mock(Parent.class));
            fail("Should break, because controller is of different type!");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Controller is not an instance of IViewController!");
        }
    }
}