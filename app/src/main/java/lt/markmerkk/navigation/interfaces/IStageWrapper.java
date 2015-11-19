package lt.markmerkk.navigation.interfaces;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Represents the wrapper class for the {@link javafx.stage.Stage}
 */
public interface IStageWrapper {
    /**
     * Gets core stage under the wrapper.
     *
     * @return stage
     */
    Stage getStage();

    /**
     * Wrapper method for stage
     * @param scene
     */
    void setScene(Scene scene);

    /**
     * Wrapper method for stage
     */
    void setTitle(String title);

    /**
     * Wrapper method for stage
     */
    void setWidth(double width);

    /**
     * Wrapper method for stage
     */
    void setHeight(double height);

    /**
     * Wrapper method for stage
     */
    double getWidth();

    /**
     * Wrapper method for stage
     */
    double getHeight();

    /**
     * Wrapper method for stage
     */
    void show();
}
