package lt.markmerkk;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.controllers.BaseController;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application implements BaseController.BaseControllerDelegate {
    public static int SCENE_HEIGHT = 320;
    public static int SCENE_WIDTH = 480;
    protected Stage primaryStage;
    private ArrayList<BaseController> sceneInstances;
    public static final String SCENE_XML_MAIN = "/sample.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        pushScene(SCENE_XML_MAIN, null);
        primaryStage.setWidth(SCENE_WIDTH);
        primaryStage.setHeight(SCENE_HEIGHT);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // System is shutting down
        for (BaseController baseController : sceneInstances)
            baseController.pause(); // Pausing all scenes
        for (BaseController baseController : sceneInstances)
            baseController.destroy(); // Destroying all scenes
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Shows scene by its provided xml layout.
     * If this scene already exist in the hashmap of scenes it loads the older one.
     * If scene does not exits, the scene is created anew.
     *
     * @param sceneXml provided xml that should be initialized
     */
    @Override
    public BaseController pushScene(String sceneXml, Object data) {
        if (sceneInstances == null)
            sceneInstances = new ArrayList<BaseController>();
        if (sceneInstances.size() > 0) {
            BaseController baseController = sceneInstances.get(sceneInstances.size() - 1);
            baseController.pause(); // Pausing current scene
        }
        sceneInstances.add(initSceneToHash(sceneXml));
        primaryStage.setTitle("HG");
        BaseController newController = sceneInstances.get(sceneInstances.size() - 1);
        newController.create(data); // Creating new one
        newController.resume(); // Resuming new one
        primaryStage.setScene(newController.getMasterScene());
        primaryStage.setWidth(getStage().getWidth());
        primaryStage.setHeight(getStage().getHeight());
        primaryStage.show();
        return newController;
    }

    @Override
    public void popScene() {
        BaseController baseController = sceneInstances.get(sceneInstances.size() - 1);
        baseController.pause(); // Pausing poped scene
        baseController.destroy(); // Destroying poped scene
        sceneInstances.remove(baseController);
        BaseController oldScene = sceneInstances.get(sceneInstances.size() - 1);
        oldScene.resume(); // Resuming old scene
        primaryStage.setScene(oldScene.getMasterScene());
        primaryStage.setWidth(getStage().getWidth());
        primaryStage.setHeight(getStage().getHeight());
        primaryStage.show();
    }

    /**
     * Initializes a scene by its xml layout and returns its controller
     *
     * @param xmlLayout provided xml layout that should be initialized
     * @return initialized and ready controller w`ith its scene reference inside
     */
    public BaseController initSceneToHash(String xmlLayout) {
        try {
            Scene scene;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
            Parent root = loader.load();
            BaseController controller = loader.getController();
            scene = new Scene(root);
            controller.setupController(this, scene, primaryStage);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Stage getStage() {
        return primaryStage;
    }
}
