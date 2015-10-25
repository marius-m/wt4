package lt.markmerkk.controllers.interfaces;

import javafx.stage.Stage;
import lt.markmerkk.controllers.BaseController;

/**
 * An interface that helps set up controller and its core navigation events
 */
public interface IViewNavigationController {
    /**
     * Returns current stage
     * @return stage
     */
    Stage getStage();

    /**
     * Pushes new controller forward in the stack
     * @param sceneXml new controller scene layout
     * @param data additional data passed in. This is optional, null passing is valid.
     * @return new scene
     */
    IViewController pushScene(String sceneXml, Object data);

    /**
     * Pops scene out of the controller stack
     */
    void popScene();
}
