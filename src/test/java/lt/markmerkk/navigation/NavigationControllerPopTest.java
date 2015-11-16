package lt.markmerkk.navigation;

import lt.markmerkk.navigation.interfaces.IStageWrapper;
import lt.markmerkk.navigation.interfaces.IViewController;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Tests {@link NavigationController#popScene()}
 */
public class NavigationControllerPopTest extends NavigationControllerTest {

    @Override
    protected void init() {
        super.init();
        //controller.stage = mock(IStageWrapper.class);
    }

    @Test
    public void shouldNotBreakWhenNoControllersInTheList() throws Exception {
        // Arrange
        init();

        // Act
        controller.popScene();

        // Assert
    }

    @Test
    public void shouldNotPopIfItsTheLastScreen1() throws Exception {
        // Arrange
        init();
        controller.controllers.add(mock(IViewController.class));

        // Act
        controller.popScene();

        // Assert
        assertThat(controller.controllers.size()).isEqualTo(1);
    }

    @Test
    public void shouldNotPopIfItsTheLastScreen2() throws Exception {
        // Arrange
        init();
        controller.controllers.add(mock(IViewController.class));

        // Act
        controller.popScene();
        controller.popScene();
        controller.popScene();
        controller.popScene();
        controller.popScene();

        // Assert
        assertThat(controller.controllers.size()).isEqualTo(1);
    }

    @Test
    public void shouldNotPopIfItsTheLastScreen3() throws Exception {
        // Arrange
        init();
        controller.controllers.add(mock(IViewController.class));
        controller.controllers.add(mock(IViewController.class));
        controller.controllers.add(mock(IViewController.class));

        // Act
        controller.popScene();
        controller.popScene();
        controller.popScene();
        controller.popScene();
        controller.popScene();
        controller.popScene();
        controller.popScene();

        // Assert
        assertThat(controller.controllers.size()).isEqualTo(1);
    }

    @Test
    public void shouldCallProperMethodsWhenPop1() throws Exception {
        // Arrange
        init();
        IViewController firstView = mock(IViewController.class);
        IViewController secondView = mock(IViewController.class);
        controller.controllers.add(firstView);
        controller.controllers.add(secondView);

        // Act
        controller.popScene();

        // Assert
        verify(secondView).pause();
        verify(secondView).destroy();
        verify(firstView).resume();
    }
}