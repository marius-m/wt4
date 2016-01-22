package lt.markmerkk;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.MainView;
import lt.markmerkk.ui.update.UpdateLogView;

public class Main extends Application {
    public static int SCENE_WIDTH = 600;
    public static int SCENE_HEIGHT = 500;

    public Main() { }

    @Override
    public void start(Stage stage) throws Exception{

        MainView mainView = new MainView(stage);
        Scene scene = new Scene(mainView.getView());
        scene.getStylesheets().add(
            getClass().getResource("/text-field-red-border.css").toExternalForm());
        stage.setWidth(SCENE_WIDTH);
        stage.setHeight(SCENE_HEIGHT);
        stage.setMinWidth(SCENE_WIDTH);
        stage.setMinHeight(SCENE_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() throws Exception {
        super.stop();
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
