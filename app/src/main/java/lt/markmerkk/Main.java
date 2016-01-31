package lt.markmerkk;

import com.airhacks.afterburner.injection.Injector;
import com.google.common.util.concurrent.Uninterruptibles;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import com.vinumeris.updatefx.AppDirectory;
import com.vinumeris.updatefx.Crypto;
import com.vinumeris.updatefx.UpdateFX;
import com.vinumeris.updatefx.UpdateSummary;
import com.vinumeris.updatefx.Updater;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lt.markmerkk.ui.MainView;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
  public static HostServicesDelegate hostServices;
  public static final String UPDATE_DIR = "WT4Update";
  public static int VERSION = 5;
  public static int SCENE_WIDTH = 600;
  public static int SCENE_HEIGHT = 500;
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public Main() {
  }

//  private ProgressIndicator showGiantProgressWheel(Stage stage) {
//    ProgressIndicator indicator = new ProgressIndicator();
//    BorderPane borderPane = new BorderPane(indicator);
//    borderPane.setMinWidth(640);
//    borderPane.setMinHeight(480);
//    Button pinButton = new Button();
//    pinButton.setText("Pin to version 1");
//    pinButton.setOnAction(event -> {
//      UpdateFX.pinToVersion(AppDirectory.dir(), 1);
//      UpdateFX.restartApp();
//    });
//    HBox box = new HBox(new Label("Version " + VERSION), pinButton);
//    box.setSpacing(10);
//    box.setAlignment(Pos.CENTER_LEFT);
//    box.setPadding(new Insets(10));
//    borderPane.setTop(box);
//    Scene scene = new Scene(borderPane);
//    stage.setScene(scene);
//    return indicator;
//  }

  @Override
  public void start(Stage stage) throws Exception {
    Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());
    AppDirectory.initAppDir(UPDATE_DIR);

    hostServices = HostServicesFactory.getInstance(this);

//    log.info("Hello World! This is version " + VERSION);
//    ProgressIndicator indicator = showGiantProgressWheel(stage);
//    indicator.progressProperty().bind(updater.progressProperty());

//    log.info("Checking for updates!");
//    updater.setOnSucceeded(event -> {
//      try {
//        UpdateSummary summary = updater.get();
//        log.info("Summary: " + summary);
//        if (summary.descriptions.size() > 0) {
//          log.info("One liner: {}", summary.descriptions.get(0).getOneLiner());
//          log.info("{}", summary.descriptions.get(0).getDescription());
//        }
//        if (summary.highestVersion > VERSION) {
//          log.info("Restarting to get version " + summary.highestVersion);
//          UpdateFX.pinToVersion(AppDirectory.dir(), summary.highestVersion);
//          UpdateFX.restartApp();
//          //if (UpdateFX.getVersionPin(AppDirectory.dir()) == 0)
//        }
//      } catch (Throwable e) {
//        log.error("oops", e);
//      }
//    });
//    updater.setOnFailed(event -> {
//      log.error("Update error: {}", updater.getException());
//      updater.getException().printStackTrace();
//    });
//    indicator.setOnMouseClicked(ev -> UpdateFX.restartApp());
//    new Thread(updater, "UpdateFX Thread").start();
//    stage.show();

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

  @Override
  public void stop() throws Exception {
    super.stop();
    hostServices = null;
    Injector.forgetAll();
  }

  public static void main(String[] args) throws IOException {
    AppDirectory.initAppDir(UPDATE_DIR);
    UpdateFX.bootstrap(Main.class, AppDirectory.dir(), args);
  }

  public static void realMain(String[] args) {
    launch(args);
  }

}
