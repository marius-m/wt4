package lt.markmerkk;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {
    public static int SCENE_HEIGHT = 320;
    public static int SCENE_WIDTH = 480;
    public static final String SCENE_XML_MAIN = "/sample.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
//        primaryStage.setWidth(SCENE_WIDTH);
//        primaryStage.setHeight(SCENE_HEIGHT);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
