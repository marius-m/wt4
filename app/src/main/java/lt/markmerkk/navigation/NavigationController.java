package lt.markmerkk.navigation;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lt.markmerkk.navigation.interfaces.*;

import java.util.ArrayList;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Represents the basic logic for controller navigation for the scene.
 * This holds a stack of navigation, and lets them push forward and backward when needed.
 */
public class NavigationController implements IViewNavigationController {

    //IStageWrapper stage;
    ISceneLoader sceneLoader;
    IPaneHandler paneHandler;
    ArrayList<IViewController> controllers;

    public NavigationController() {
        controllers = new ArrayList<IViewController>();
    }

    public void stop() {
        // System is shutting down
        for (IViewController baseController : controllers)
            baseController.pause(); // Pausing all controllers
        for (IViewController baseController : controllers)
            baseController.destroy(); // Destroying all controllers
        Platform.exit();
    }

    public void start(IPaneHandler paneHandler) {
        this.paneHandler = paneHandler;
    }

    @Override
    public IViewController pushScene(String sceneXml, Object data) {
        if (sceneXml == null)
            throw new IllegalArgumentException("Cannot push a scene without fxml path!");
        if (controllers.size() > 0) {
            IViewController baseController = controllers.get(controllers.size() - 1);
            baseController.pause();
        }
        controllers.add(initLayout(sceneXml));
        IViewController newController = controllers.get(controllers.size() - 1);
        newController.create(data);
        newController.resume();
        paneHandler.show();
//        stage.setScene(newController.masterScene());
//        stage.show();
        return newController;
    }

    @Override
    public void popScene() {
        if (controllers.size() <= 1)
            return;
        IViewController baseController = controllers.get(controllers.size() - 1);
        baseController.pause();
        baseController.destroy();
        controllers.remove(baseController);
        IViewController oldController = controllers.get(controllers.size() - 1);
        oldController.resume();
        paneHandler.show();
//        stage.setScene(oldController.masterScene());
//        stage.show();
    }


//    @Override
//    public Stage getStage() {
//        return stage;
//    }

    //region Convenience

    /**
     * Initializes a scene by its xml layout and returns its controller
     *
     * @param xmlLayout provided xml layout that should be initialized
     * @return initialized and ready controller w`ith its scene reference inside
     */
    IViewController initLayout(String xmlLayout) {
        if (xmlLayout == null)
            throw new IllegalArgumentException("Error getting scene path!");
        Scene scene;
        Pane pane = (Pane) sceneLoader.load(xmlLayout);
        IViewController controller = sceneLoader.getController(pane);
        //scene = initScene(pane);
        paneHandler.init(pane);
        controller.setup(this);
        return controller;
    }

    /**
     * Initializes a scene from a parent.
     * Wrapper method.
     *
     * @param parent provided parent
     * @return new initialized scene
     */
    Scene initScene(Parent parent) {
        return new Scene(parent);
    }

    //endregion

}
