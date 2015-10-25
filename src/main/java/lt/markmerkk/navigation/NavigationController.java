package lt.markmerkk.navigation;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lt.markmerkk.navigation.interfaces.ISceneLoader;
import lt.markmerkk.navigation.interfaces.IStageWrapper;
import lt.markmerkk.navigation.interfaces.IViewController;
import lt.markmerkk.navigation.interfaces.IViewNavigationController;

import java.util.ArrayList;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Represents the basic logic for controller navigation for the scene.
 * This holds a stack of navigation, and lets them push forward and backward when needed.
 */
public class NavigationController implements IViewNavigationController {

    ISceneLoader sceneLoader;
    IStageWrapper stage;
    ArrayList<IViewController> scenes;

    public NavigationController() {
        scenes = new ArrayList<IViewController>();
    }

    public void stop() {
        // System is shutting down
        for (IViewController baseController : scenes)
            baseController.pause(); // Pausing all scenes
        for (IViewController baseController : scenes)
            baseController.destroy(); // Destroying all scenes
        Platform.exit();
    }

    public void start(IStageWrapper primaryStage) {
        this.stage = primaryStage;
        //pushScene(SCENE_XML_MAIN, null);
    }

    @Override
    public IViewController pushScene(String sceneXml, Object data) {
        if (sceneXml == null)
            throw new IllegalArgumentException("Cannot push a scene without fxml path!");
        if (scenes.size() > 0) {
            IViewController baseController = scenes.get(scenes.size() - 1);
            baseController.pause(); // Pausing current scene
        }
        scenes.add(initLayout(sceneXml));
        stage.setTitle("HG");
        IViewController newController = scenes.get(scenes.size() - 1);
        newController.create(data); // Creating new one
        newController.resume(); // Resuming new one
        stage.setScene(newController.masterScene());
        stage.setWidth(stage.getWidth());
        stage.setHeight(stage.getHeight());
        stage.show();
        return newController;
    }

    @Override
    public void popScene() {
        if (scenes.size() <= 1)
            return;
        IViewController baseController = scenes.get(scenes.size() - 1);
        baseController.pause();
        baseController.destroy();
        scenes.remove(baseController);
        IViewController oldController = scenes.get(scenes.size() - 1);
        oldController.resume();
        stage.setScene(oldController.masterScene());
        stage.setWidth(stage.getWidth());
        stage.setHeight(stage.getHeight());
        stage.show();
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
        Parent parent = sceneLoader.load(xmlLayout);
        IViewController controller = sceneLoader.getController(parent);
        scene = initScene(parent);
        controller.setup(this, scene);
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
