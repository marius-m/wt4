package lt.markmerkk.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Tests {@link SceneLoader#load(String)} method
 */
public class SceneLoaderLoadTest {
    private SceneLoader loader;

    //region Helper

    private void init() {
        loader = new SceneLoader();
    }

    //endregion

    @Test
    public void shouldInitProperly() throws Exception {
        // Arrange
        init();

        // Act
        // Assert
        assertThat(loader).isNotNull();
    }

    @Test
    public void shouldReturnNullWhenPassingNull() throws Exception {
        // Arrange
        init();

        // Act
        // Assert
        assertThat(loader.load(null)).isNull();
    }

    @Test
    public void shouldReturnNullWhenInvalidPath() throws Exception {
        // Arrange
        init();
        loader.loader = mock(FXMLLoader.class);
        doThrow(new IOException("Cant find resource")).when(loader.loader).load();

        // Act
        // Assert
        assertThat(loader.load("/InvalidResource.fxml")).isNull();
    }

    @Test
    public void shouldReturnValidWhenPathIsValid() throws Exception {
        // Arrange
        init();
        loader.loader = mock(FXMLLoader.class);
        doReturn(mock(Parent.class)).when(loader.loader).load();

        // Act
        // Assert
        assertThat(loader.load("/SomeValidPath.fxml")).isNotNull();
    }
}