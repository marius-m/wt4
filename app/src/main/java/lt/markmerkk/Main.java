package lt.markmerkk;

import com.airhacks.afterburner.injection.Injector;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.clock.ClockView;
import lt.markmerkk.controllers.BaseController;

public class Main extends Application {
    public static int SCENE_HEIGHT = 500;
    public static int SCENE_WIDTH = 600;

    public Main() { }

    @Override
    public void start(Stage stage) throws Exception{

        ClockView clockView = new ClockView();
        Scene scene = new Scene(clockView.getView());

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
