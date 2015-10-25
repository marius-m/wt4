package lt.markmerkk.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: marius
 * Date: 10/23/13
 * Time: 8:47 PM
 */
public abstract class BaseController {

    public interface BaseControllerDelegate {
        Stage getStage();
        BaseController pushScene(String sceneXml, Object data);
        void popScene();
    }

    protected BaseControllerDelegate mMasterListener;
    protected Scene mMasterScene;

    public BaseController() { }

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
