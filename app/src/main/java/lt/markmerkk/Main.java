package lt.markmerkk;

import com.airhacks.afterburner.injection.Injector;
import com.google.common.util.concurrent.Uninterruptibles;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.MainView;
import lt.markmerkk.ui.update.UpdateLogView;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    public static int VERSION = 1;
    public static int SCENE_WIDTH = 600;
    public static int SCENE_HEIGHT = 500;
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public Main() { }

    private ProgressIndicator showGiantProgressWheel(Stage stage) {
        ProgressIndicator indicator = new ProgressIndicator();
        BorderPane borderPane = new BorderPane(indicator);
        borderPane.setMinWidth(640);
        borderPane.setMinHeight(480);
        Button pinButton = new Button();
        pinButton.setText("Pin to version 1");
        pinButton.setOnAction(event -> {
            UpdateFX.pinToVersion(AppDirectory.dir(), 1);
            UpdateFX.restartApp();
        });
        HBox box = new HBox(new Label("Version " + VERSION), pinButton);
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        borderPane.setTop(box);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        return indicator;
    }

    @Override
    public void start(Stage stage) throws Exception{
        // For some reason the JavaFX launch process results in us losing the thread context class loader: reset it.
        Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());
        // Must be done twice for the times when we come here via realMain.
        AppDirectory.initAppDir("UpdateFX Example App");

        log.info("Hello World! This is version " + VERSION);

        ProgressIndicator indicator = showGiantProgressWheel(stage);

        List<ECPoint> pubkeys = Crypto.decode(
            "020044E154894596A94EF649DC203358C27A310DDBE4D22646AC56BC8FB0BFBAFB",
            "0292BAC95D764ADBF15367ABE490708753FE2E92760F7C9FF6951EE558F57AE407");
        Updater updater = new Updater(URI.create("http://localhost:8000/files"), "main/" + VERSION,
            AppDirectory.dir(), UpdateFX.findCodePath(Main.class), pubkeys, 1) {
            @Override
            protected void updateProgress(long workDone, long max) {
                super.updateProgress(workDone, max);
                // Give UI a chance to show.
                Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            }
        };

        indicator.progressProperty().bind(updater.progressProperty());

        log.info("Checking for updates!");
        updater.setOnSucceeded(event -> {
            try {
                UpdateSummary summary = updater.get();
                if (summary.descriptions.size() > 0) {
                    log.info("One liner: {}", summary.descriptions.get(0).getOneLiner());
                    log.info("{}", summary.descriptions.get(0).getDescription());
                }
                if (summary.highestVersion > VERSION) {
                    log.info("Restarting to get version " + summary.highestVersion);
                    if (UpdateFX.getVersionPin(AppDirectory.dir()) == 0)
                        UpdateFX.restartApp();
                }
            } catch (Throwable e) {
                log.error("oops", e);
            }
        });
        updater.setOnFailed(event -> {
            log.error("Update error: {}", updater.getException());
            updater.getException().printStackTrace();
        });

        indicator.setOnMouseClicked(ev -> UpdateFX.restartApp());

        new Thread(updater, "UpdateFX Thread").start();
        stage.show();

//        MainView mainView = new MainView(stage);
//        Scene scene = new Scene(mainView.getView());
//        scene.getStylesheets().add(
//            getClass().getResource("/text-field-red-border.css").toExternalForm());
//        stage.setWidth(SCENE_WIDTH);
//        stage.setHeight(SCENE_HEIGHT);
//        stage.setMinWidth(SCENE_WIDTH);
//        stage.setMinHeight(SCENE_HEIGHT);
//        stage.setScene(scene);
//        stage.show();
    }

    @Override public void stop() throws Exception {
        super.stop();
        Injector.forgetAll();
    }

    public static void main(String[] args) throws IOException {
        AppDirectory.initAppDir("UpdateFX Example App");
        UpdateFX.bootstrap(Main.class, AppDirectory.dir(), args);
    }

    public static void realMain(String[] args) {
        launch(args);
    }

}
