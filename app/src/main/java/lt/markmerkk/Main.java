package lt.markmerkk;

import com.airhacks.afterburner.injection.Injector;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import com.vinumeris.updatefx.AppDirectory;
import com.vinumeris.updatefx.UpdateFX;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.ui.MainView;
import lt.markmerkk.utils.FirstSettings;
import lt.markmerkk.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
  public static final String LOG_LAYOUT = "%d{ABSOLUTE} %5p %c{1}:%L - %m%n";
  public static boolean DEBUG = true;
  public static String CFG_PATH;
  public static HostServicesDelegate hostServices;
  public static final String UPDATE_DIR = "WT4Update";
  public static int VERSION_CODE = 1; // Will be updated on init
  public static String VERSION_NAME = "Unknown";  // Will be updated on init
  public static int SCENE_WIDTH = 600;
  public static int SCENE_HEIGHT = 500;

  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  private RollingFileAppender fileAppender;
  private RollingFileAppender errorAppender;

  public Main() { }

  @Override
  public void start(Stage stage) throws Exception {
    DEBUG = ("false".equals(System.getProperty("release")));
    logger.info("Running in DEBUG=" + DEBUG);
    Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());
    initVersionSettings();
    if (isFirstLaunch()) {
      AppDirectory.initAppDir(UPDATE_DIR);
//      UpdateFX.restartApp();
//      return;
    }
    initStaticPaths();
    initLoggerSettings();

    hostServices = HostServicesFactory.getInstance(this);

    MainView mainView = new MainView(stage);
    Scene scene = new Scene(mainView.getView());
    String cssResource1 = getClass().getResource("/text-field-red-border.css").toExternalForm();
    scene.getStylesheets().add(cssResource1);
    logger.debug("Loading external resource: {}", cssResource1);
    stage.setWidth(SCENE_WIDTH);
    stage.setHeight(SCENE_HEIGHT);
    stage.setMinWidth(SCENE_WIDTH);
    stage.setMinHeight(SCENE_HEIGHT);
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    org.apache.log4j.Logger.getRootLogger().removeAppender(fileAppender);
    org.apache.log4j.Logger.getRootLogger().removeAppender(errorAppender);
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

  //region Convenience

  /**
   * Prepares logger settings
   * @throws IOException
   */
  private void initLoggerSettings() throws IOException {
    PropertyConfigurator.configure(getClass().getResource("/custom_log4j.properties"));
    fileAppender = new RollingFileAppender(new PatternLayout(LOG_LAYOUT), CFG_PATH + "info.log", true);
    fileAppender.setMaxFileSize("1000KB");
    fileAppender.setMaxBackupIndex(1);
    fileAppender.setThreshold(Priority.INFO);
    org.apache.log4j.Logger.getRootLogger().addAppender(fileAppender);

    errorAppender = new RollingFileAppender(new PatternLayout(LOG_LAYOUT), CFG_PATH + "debug.log", true);
    errorAppender.setMaxFileSize("100000KB");
    errorAppender.setMaxBackupIndex(1);
    errorAppender.setThreshold(Priority.toPriority(Priority.ALL_INT));
    org.apache.log4j.Logger.getRootLogger().addAppender(errorAppender);
  }

  /**
   * Prepares an update directory
   */
  static boolean isFirstLaunch() throws IOException {
    if (DEBUG) {
      logger.info("Running debug version! Skipping first launch check!");
      return false;
    }
    Path updatePath = AppDirectory.initAppDir(UPDATE_DIR);
    FirstSettings firstSettings = new FirstSettings();
    firstSettings.load();
    if (firstSettings.isFirst()) {
      File updateDir = updatePath.toFile();
      Utils.delete(updateDir);
      firstSettings.save();
      return true;
    }
    return false;
  }

  /**
   * Initializes app version settings
   * @throws IOException
   */
  void initVersionSettings() throws IOException {
    Properties versionProperties = new Properties();
    InputStream resourceAsStream = getClass().getResourceAsStream("/version.properties");
    versionProperties.load(resourceAsStream);
    VERSION_CODE = Integer.parseInt(versionProperties.getProperty("version_code"));
    VERSION_NAME = versionProperties.getProperty("version_name");
    logger.info("Running version %s with version code %d", VERSION_NAME, VERSION_CODE);
  }

  /**
   * Initializes main constant static's for later use
   */
  private void initStaticPaths() {
    String home = System.getProperty("user.home");
    try {
      File file = new File(home + ((DEBUG) ? "/.wt4_debug/" : "/.wt4/"));
      FileUtils.forceMkdir(file);
      CFG_PATH = file.getAbsolutePath()+"/";
      CFG_PATH = (CFG_PATH == null) ? "" : CFG_PATH;
    } catch (IOException e) { }
  }

  //endregion

}
