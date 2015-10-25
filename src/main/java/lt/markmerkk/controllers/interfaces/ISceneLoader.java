package lt.markmerkk.controllers.interfaces;

import javafx.scene.Parent;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Represents core methods for loading scenes
 */
public interface ISceneLoader {
    /**
     * Loads the scene and returns it as a root view.
     * @return view
     * @param xmlPath path of the xml to load
     */
    Parent load(String xmlPath);

    /**
     * Gets the controller from the loaded view.
     * @param parent in parent instance. This is not needed internally, but acts like a validation
     *               when trying to get controller before {@link #load(String)} method is called.
     * @return controller
     */
    IViewController getController(Parent parent);
}
