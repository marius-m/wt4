package lt.markmerkk;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lt.markmerkk.navigation.NavigationController;
import lt.markmerkk.navigation.interfaces.IViewController;

public class Main extends Application {
    public static int SCENE_HEIGHT = 320;
    public static int SCENE_WIDTH = 480;
    public static final String SCENE_XML_MAIN = "/sample.fxml";

    public Main() {
        NavigationController navigationController = new NavigationController();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SCENE_XML_MAIN));
        Parent parent = loader.load();

        //Parent parent2 = new FXMLLoader(getClass().getResource(SCENE_XML_MAIN)).load();
        //((VBox)parent.lookup("#work")).getChildren().add(parent2);

        stage.setTitle("WT4");
        stage.setScene(new Scene(parent));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
