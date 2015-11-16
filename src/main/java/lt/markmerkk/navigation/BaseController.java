package lt.markmerkk.navigation;

import javafx.scene.Scene;
import lt.markmerkk.navigation.interfaces.IViewController;
import lt.markmerkk.navigation.interfaces.IViewNavigationController;

/**
 * Base implementation of the controller, that's life-cycle is controller in ... .
 */
// Todo : complete documentation!
public abstract class BaseController implements IViewController {

    public BaseController() { }

    @Override
    public void setup(IViewNavigationController listener) {

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
