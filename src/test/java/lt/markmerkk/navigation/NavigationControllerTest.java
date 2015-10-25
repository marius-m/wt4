package lt.markmerkk.navigation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Base test class for {@link NavigationController}
 */
public class NavigationControllerTest {

    protected NavigationController controller;

    protected void init() {
        controller = spy(new NavigationController());
    }

    @Test
    public void shouldInitArray() throws Exception {
        // Arrange
        controller = new NavigationController();
        // Act
        // Assert
        assertThat(controller.scenes).isNotNull();
    }

}