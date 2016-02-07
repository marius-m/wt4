package lt.markmerkk;

import com.airhacks.afterburner.injection.Injector;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.ui.MainView;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
  public static final boolean DEBUG = true;
  public static String CFG_PATH;
  public static HostServicesDelegate hostServices;
  public static final String UPDATE_DIR = "WT4Update";
  public static int VERSION = 1;
  public static int SCENE_WIDTH = 600;
  public static int SCENE_HEIGHT = 500;

  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  private RollingFileAppender fileAppender;

  public Main() {
  }

  @Override
  public void start(Stage stage) throws Exception {
    // Setting up file paths
    String home = System.getProperty("user.home");
    try {
      File file = new File(home + ((DEBUG) ? "/.wt4_debug/" : "/.wt4/"));
      FileUtils.forceMkdir(file);
      CFG_PATH = file.getAbsolutePath() + "/";
    } catch (IOException e) {
    }

    // After bootstrap function log4j fails to load configuration. Need to persist config.
    PropertyConfigurator.configure(getClass().getResource("/custom_log4j.properties"));
//    SimpleLayout layout = new SimpleLayout();
//    fileAppender = new RollingFileAppender(layout, CFG_PATH + "info.log", true);
//    fileAppender.setMaxFileSize("1000KB");
//    fileAppender.setMaxBackupIndex(1);
//    fileAppender.setThreshold(Priority.INFO);
//    org.apache.log4j.Logger.getRootLogger().addAppender(fileAppender);


    hostServices = HostServicesFactory.getInstance(this);

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
//    org.apache.log4j.Logger.getRootLogger().removeAppender(fileAppender);
    super.stop();
    hostServices = null;
    Injector.forgetAll();
  }

  public static void main(String[] args) throws IOException {
    launch(args);
  }

  public static void realMain(String[] args) {
  }

}
