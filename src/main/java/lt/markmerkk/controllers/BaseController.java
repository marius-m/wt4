package lt.markmerkk.controllers;

import javafx.scene.Scene;
import lt.markmerkk.controllers.interfaces.IViewController;
import lt.markmerkk.controllers.interfaces.IViewNavigationController;

/**
 * Base implementation of the controller, that's life-cycle is controller in ... .
 */
// Todo : complete documentation!
public abstract class BaseController implements IViewController {

    public BaseController() { }

    @Override
    public void setup(IViewNavigationController listener, Scene scene) {

    }

    @Override
    public Scene masterScene() {
        return null;
    }

    @Override
    public void create(Object data) {

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
