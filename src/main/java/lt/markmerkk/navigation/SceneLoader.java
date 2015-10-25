package lt.markmerkk.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lt.markmerkk.navigation.interfaces.ISceneLoader;
import lt.markmerkk.navigation.interfaces.IViewController;

import java.io.IOException;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Represents the scene loading class
 */
public class SceneLoader implements ISceneLoader {
    FXMLLoader loader;

    @Override
    public Parent load(String xmlPath) {
        if (xmlPath == null)
            return null;
        if (loader == null)
            loader = new FXMLLoader(getClass().getResource(xmlPath));
        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public IViewController getController(Parent parent) {
        if (parent == null)
            throw new IllegalArgumentException("No parent is specified!");
        Object controller = loader.getController();
        if (!(controller instanceof IViewController))
            throw new IllegalArgumentException("Controller is not an instance of IViewController!");
        return (IViewController) controller;
    }

}
