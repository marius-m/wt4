package lt.markmerkk.navigation;

import javafx.scene.Parent;
import javafx.scene.Scene;
import lt.markmerkk.navigation.interfaces.ISceneLoader;
import lt.markmerkk.navigation.interfaces.IViewController;
import lt.markmerkk.navigation.interfaces.IViewNavigationController;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 10/25/15.
 */
public class NavigationControllerInitLayoutTest extends NavigationControllerTest {
    @Test
    public void shouldBreakWhenNullXmlPath() throws Exception {
        // Arrange
        init();

        // Act
        // Assert
        try {
            controller.initLayout(null);
            fail("Should not init scene when no xml path is defined!");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Error getting scene path!");
        }
    }

    @Test
    public void shouldReturnValidController() throws Exception {
        // Arrange
        init();
        controller.sceneLoader = mock(ISceneLoader.class);
        doReturn(mock(Scene.class)).when(controller).initScene(any(Parent.class));
        doReturn(mock(Parent.class)).when(controller.sceneLoader).load(anyString());
        doReturn(mock(IViewController.class)).when(controller.sceneLoader).getController(any(Parent.class));
        // Act
        // Assert
        assertThat(controller.initLayout("/SomeScene.fxml")).isNotNull();
    }

    @Test
    public void shouldCallSetupInInitController() throws Exception {
        // Arrange
        init();
        IViewController viewController = mock(IViewController.class);
        controller.sceneLoader = mock(ISceneLoader.class);
        doReturn(mock(Scene.class)).when(controller).initScene(any(Parent.class));
        doReturn(mock(Parent.class)).when(controller.sceneLoader).load(anyString());
        doReturn(viewController).when(controller.sceneLoader).getController(any(Parent.class));
        // Act
        controller.initLayout("/SomeScene.fxml");
        // Assert
        verify(viewController).setup(any(IViewNavigationController.class));
    }
}
