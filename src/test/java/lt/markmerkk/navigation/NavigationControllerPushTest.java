package lt.markmerkk.navigation;

import lt.markmerkk.navigation.interfaces.IStageWrapper;
import lt.markmerkk.navigation.interfaces.IViewController;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Tests {@link NavigationController#pushScene(String, Object)}
 */
public class NavigationControllerPushTest extends NavigationControllerTest {

    @Override
    protected void init() {
        super.init();
        controller.stage = mock(IStageWrapper.class);
    }

    @Test
    public void shouldBreakWhenPushWithoutXml() throws Exception {
        // Arrange
        init();

        // Act
        // Assert
        try {
            controller.pushScene(null, "SomeRandomObject");
            fail("Should not push a scene without xml path");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot push a scene without fxml path!");
        }
    }

    @Test
    public void shouldPushValid() throws Exception {
        // Arrange
        init();
        IViewController viewController = mock(IViewController.class);
        doReturn(viewController).when(controller).initLayout("/ValidXml.fxml");

        // Act
        // Assert
        assertThat(controller.pushScene("/ValidXml.fxml", null)).isNotNull();
    }

    @Test
    public void shouldCallCreateOnFirstPush() throws Exception {
        // Arrange
        init();
        IViewController viewController = mock(IViewController.class);
        doReturn(viewController).when(controller).initLayout("/ValidXml.fxml");

        // Act
        controller.pushScene("/ValidXml.fxml", null);
        // Assert
        verify(viewController).create(null);
    }

    @Test
    public void shouldCallResumeOnFirstPush() throws Exception {
        // Arrange
        init();
        IViewController viewController = mock(IViewController.class);
        doReturn(viewController).when(controller).initLayout("/ValidXml.fxml");

        // Act
        controller.pushScene("/ValidXml.fxml", null);
        // Assert
        verify(viewController).create(null);
    }

    @Test
    public void shouldCallProperMethodsWhenPush1() throws Exception {
        // Arrange
        init();
        IViewController firstView = mock(IViewController.class);
        IViewController secondView = mock(IViewController.class);
        doReturn(firstView)
                .doReturn(secondView)
                .when(controller).initLayout("/ValidXml.fxml");

        // Act
        // Assert
        controller.pushScene("/ValidXml.fxml", null);
        verify(firstView).create(null);
        verify(firstView).resume();
        controller.pushScene("/ValidXml.fxml", null);
        verify(firstView).pause();
        verify(secondView).create(null);
        verify(secondView).resume();
    }

}