package lt.markmerkk.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.controllers.interfaces.IViewController;
import lt.markmerkk.controllers.interfaces.IViewNavigationController;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Represents the basic logic for controller navigation for the scene.
 * This holds a stack of controllers, and lets them push forward and backward when needed.
 */
public class NavigationController implements IViewNavigationController {
    FXMLLoader loader;

    Stage stage;
    ArrayList<IViewController> sceneInstances;

    public void stop() {
        // System is shutting down
        for (IViewController baseController : sceneInstances)
            baseController.pause(); // Pausing all scenes
        for (IViewController baseController : sceneInstances)
            baseController.destroy(); // Destroying all scenes
        Platform.exit();
    }

    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        //pushScene(SCENE_XML_MAIN, null);
    }

    /**
     * Shows scene by its provided xml layout.
     * If this scene already exist in the hashmap of scenes it loads the older one.
     * If scene does not exits, the scene is created anew.
     *
     * @param sceneXml provided xml that should be initialized
     */
    @Override
    public IViewController pushScene(String sceneXml, Object data) {
        if (sceneInstances == null)
            sceneInstances = new ArrayList<IViewController>();
        if (sceneInstances.size() > 0) {
            IViewController baseController = sceneInstances.get(sceneInstances.size() - 1);
            baseController.pause(); // Pausing current scene
        }
        sceneInstances.add(initScene(sceneXml));
        stage.setTitle("HG");
        IViewController newController = sceneInstances.get(sceneInstances.size() - 1);
        newController.create(data); // Creating new one
        newController.resume(); // Resuming new one
        stage.setScene(newController.masterScene());
        stage.setWidth(getStage().getWidth());
        stage.setHeight(getStage().getHeight());
        stage.show();
        return newController;
    }

    @Override
    public void popScene() {
        IViewController baseController = sceneInstances.get(sceneInstances.size() - 1);
        baseController.pause();
        baseController.destroy();
        sceneInstances.remove(baseController);
        IViewController oldScene = sceneInstances.get(sceneInstances.size() - 1);
        oldScene.resume();
        stage.setScene(oldScene.masterScene());
        stage.setWidth(getStage().getWidth());
        stage.setHeight(getStage().getHeight());
        stage.show();
    }


    @Override
    public Stage getStage() {
        return stage;
    }

    /**
     * Initializes a scene by its xml layout and returns its controller
     *
     * @param xmlLayout provided xml layout that should be initialized
     * @return initialized and ready controller w`ith its scene reference inside
     */
    IViewController initScene(String xmlLayout) {
        try {
            Scene scene;
            Parent root = loader.load();
            IViewController controller = loader.getController();
            scene = new Scene(root);
            controller.setup(this, scene);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads the controller with a layout from the local resource storage
     * @param xmlLayout provided xml path
     * @return loaded controller
     */
    IViewController loadController(String xmlLayout) {
        if (xmlLayout == null)
            return null;
        if (loader == null)
            loader = new FXMLLoader(getClass().getResource(xmlLayout));
        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
