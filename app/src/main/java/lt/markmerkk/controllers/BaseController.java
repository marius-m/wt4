package lt.markmerkk.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.storage2.entities.SimpleLog;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;

/**
 * Created with IntelliJ IDEA.
 * User: marius
 * Date: 10/23/13
 * Time: 8:47 PM
 */
public abstract class BaseController {
    protected final DBProdExecutor executor;

    public interface BaseControllerDelegate {
        Stage getStage();
        BaseController pushScene(String sceneXml, Object data);
        void popScene();
    }

    protected BaseControllerDelegate masterListener;
    protected Scene masterScene;

    public BaseController() {
        // Initializing database
        executor = new DBProdExecutor();
        executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    }

    public void setupController(BaseControllerDelegate listener, Scene scene, Stage primaryStage) {
        masterListener = listener;
        masterScene = scene;
    }

    public Scene getMasterScene() {
        return masterScene;
    }

    public void create(Object data) {
//        System.out.println("Create:"+getClass().getSimpleName());
    }

    public void destroy() {
//        System.out.println("Destroy:"+getClass().getSimpleName());
    }

    public void resume() {
//        System.out.println("Resume:"+getClass().getSimpleName());
    }

    public void pause() {
//        System.out.println("Pause:"+getClass().getSimpleName());
    }

}
