package lt.markmerkk;

import com.sun.javafx.application.HostServicesDelegate;
import com.vinumeris.updatefx.AppDirectory;
import com.vinumeris.updatefx.UpdateFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.afterburner.InjectorNoDI;
import lt.markmerkk.dagger.components.AppComponent;
import lt.markmerkk.dagger.components.DaggerAppComponent;
import lt.markmerkk.interactors.KeepAliveInteractor;
import lt.markmerkk.interactors.SyncController;
import lt.markmerkk.utils.WorldEvents;
import lt.markmerkk.ui.MainView;
import lt.markmerkk.utils.FirstSettings;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.tracker.SimpleTracker;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class Main extends Application {
  public static final String LOG_LAYOUT_DEBUG = "%d{dd-MMM-yyyy HH:mm:ss} %5p %c{1}:%L - %m%n";
  public static final String LOG_LAYOUT_PROD = "%d{dd-MMM-yyyy HH:mm:ss} %m%n";
//  public static boolean DEBUG = true;
//  public static String CFG_PATH;
  public static HostServicesDelegate hostServices;
  public static final String UPDATE_DIR = "WT4Update";

  public static int VERSION_CODE = 1; // Will be updated on init
  public static String VERSION_NAME = "Unknown";  // Will be updated on init
  public static String GA_KEY = null;
  public static final String APP_NAME = "WT4";

  public static int SCENE_WIDTH = 600;
  public static int SCENE_HEIGHT = 500;

  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  private RollingFileAppender fileAppenderProd;
  private RollingFileAppender fileAppenderDebug;
  private RollingFileAppender errorAppender;

  public static AppComponent sComponent = null;

  @Inject
  public UserSettings settings;
  @Inject
  public KeepAliveInteractor keepAliveInteractor;
  @Inject
  SyncController syncController;

  public Main() { }

  @Override
  public void start(Stage stage) throws Exception {
    Const.INSTANCE.setDEBUG("false".equals(System.getProperty("release")));
    logger.info("Running in DEBUG=" + Const.INSTANCE.getDEBUG());
    Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());
    initVersionSettings();
    if (isFirstLaunch()) {
      AppDirectory.initAppDir(UPDATE_DIR);
//      UpdateFX.restartApp();
//      return;
    }
    initLoggerSettings();

    Translation.getInstance(); // Initializing translations on first launch

//    hostServices = HostServicesFactory.getInstance(this);
    sComponent = DaggerAppComponent.create();
    sComponent.inject(this);

    ((WorldEvents)settings).onStart();
    keepAliveInteractor.onAttach();
    syncController.onAttach();

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
    stage.setTitle("WT4");
    SimpleTracker.getInstance().getTracker().sendEvent(
        SimpleTracker.CATEGORY_GENERIC,
        SimpleTracker.ACTION_START
    );
  }

  @Override
  public void stop() throws Exception {
    syncController.onDetach();
    keepAliveInteractor.onDetach();
    ((WorldEvents)settings).onStop();
    SimpleTracker.getInstance().getTracker().stop();
    InjectorNoDI.forgetAll();
    org.apache.log4j.Logger.getRootLogger().removeAppender(fileAppenderDebug);
    org.apache.log4j.Logger.getRootLogger().removeAppender(fileAppenderProd);
    org.apache.log4j.Logger.getRootLogger().removeAppender(errorAppender);
    super.stop();
    hostServices = null;
  }

  public static void main(String[] args) throws IOException {
    AppDirectory.initAppDir(UPDATE_DIR);
    UpdateFX.bootstrap(Main.class, AppDirectory.dir(), args);
  }

  public static void realMain(String[] args) {
    launch(args);
  }

  public static AppComponent getComponent() {
      return sComponent;
  }

  //region Convenience

  /**
   * Prepares logger settings
   * @throws IOException
   */
  private void initLoggerSettings() throws IOException {
    PropertyConfigurator.configure(getClass().getResource("/custom_log4j.properties"));
    fileAppenderProd = new RollingFileAppender(new PatternLayout(LOG_LAYOUT_PROD), Const.INSTANCE.getCfgHome() + "info_prod.log", true);
    fileAppenderProd.setMaxFileSize("100KB");
    fileAppenderProd.setMaxBackupIndex(1);
    fileAppenderProd.setThreshold(Priority.INFO);
    org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderProd);

    fileAppenderDebug = new RollingFileAppender(new PatternLayout(LOG_LAYOUT_DEBUG), Const.INSTANCE.getCfgHome() + "info.log", true);
    fileAppenderDebug.setMaxFileSize("1000KB");
    fileAppenderDebug.setMaxBackupIndex(1);
    fileAppenderDebug.setThreshold(Priority.INFO);
    org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderDebug);

    errorAppender = new RollingFileAppender(new PatternLayout(LOG_LAYOUT_DEBUG), Const.INSTANCE.getCfgHome() + "debug.log", true);
    errorAppender.setMaxFileSize("100000KB");
    errorAppender.setMaxBackupIndex(1);
    errorAppender.setThreshold(Priority.toPriority(Priority.ALL_INT));
    org.apache.log4j.Logger.getRootLogger().addAppender(errorAppender);
  }

  /**
   * Prepares an update directory
   */
  static boolean isFirstLaunch() throws IOException {
    if (Const.INSTANCE.getDEBUG()) {
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
    GA_KEY = versionProperties.getProperty("ga");
    logger.info("Running version %s with version code %d", VERSION_NAME, VERSION_CODE);
  }

  //endregion

}
