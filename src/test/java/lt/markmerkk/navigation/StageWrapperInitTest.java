package lt.markmerkk.navigation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Tests {@link StageWrapper} initialization
 */
public class StageWrapperInitTest {
    @Test
    public void shouldBreakWhenNoStage() throws Exception {
        // Arrange
        // Act
        // Assert
        try {
            new StageWrapper(null);
            fail("Should not initialize when no stage is provided!");
        } catch (Exception e) {
            assertThat(e).hasMessage("No stage provided!");
        }
    }

}