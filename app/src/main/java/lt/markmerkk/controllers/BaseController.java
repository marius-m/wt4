package lt.markmerkk.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.storage.entities.LogStorage;
import lt.markmerkk.storage.entities.Project;
import lt.markmerkk.storage.entities.Storage;
import lt.markmerkk.storage.entities.Task;
import lt.markmerkk.storage2.database.DBBaseExecutor;
import lt.markmerkk.storage2.database.DBProdExecutor;
import lt.markmerkk.storage2.entities.SimpleLog;
import lt.markmerkk.storage2.jobs.CreateJob;

/**
 * Created with IntelliJ IDEA.
 * User: marius
 * Date: 10/23/13
 * Time: 8:47 PM
 */
public abstract class BaseController {

    protected final LogStorage logStorage;
    protected final Storage<Task> taskStorage;
    protected final Storage<Project> projectStorage;
    private final DBBaseExecutor executor;

    public interface BaseControllerDelegate {
        public Stage getStage();
        public BaseController pushScene(String sceneXml, Object data);
        public void popScene();
    }

    protected BaseControllerDelegate mMasterListener;
    protected Scene mMasterScene;

    public BaseController() {
        logStorage = new LogStorage();
        taskStorage = new Storage<Task>(Task.class);
        projectStorage = new Storage<Project>(Project.class);

        // Initializing database
        executor = new DBProdExecutor();
        executor.execute(new CreateJob<SimpleLog>(SimpleLog.class));
    }

    public void setupController(BaseControllerDelegate listener, Scene scene, Stage primaryStage) {
        mMasterListener = listener;
        mMasterScene = scene;
    }

    public Scene getMasterScene() {
        return mMasterScene;
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
