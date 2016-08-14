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
import lt.markmerkk.interactors.*;
import lt.markmerkk.ui.MainView;
import lt.markmerkk.ui.version.VersioningInteractor;
import lt.markmerkk.utils.FirstSettings;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.tracker.ITracker;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main extends Application implements KeepAliveInteractor.Listener {
  public static final String LOG_LAYOUT_DEBUG = "%d{dd-MMM-yyyy HH:mm:ss} %5p %c{1}:%L - %m%n";
  public static final String LOG_LAYOUT_PROD = "%d{dd-MMM-yyyy HH:mm:ss} %m%n";
  public static HostServicesDelegate hostServices;
  public static final String UPDATE_DIR = "WT4Update";

  public static int VERSION_CODE = 1; // Will be updated on init
  public static String VERSION_NAME = "Unknown";  // Will be updated on init
  public static String GA_KEY = null;
  public static final String APP_NAME = "WT4";

  public static boolean DEBUG = false;


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
  public LogStorage logStorage;
  @Inject
  public KeepAliveInteractor keepAliveInteractor;
  @Inject
  public SyncInteractor syncInteractor;
  @Inject
  public AutoUpdateInteractor autoUpdateInteractor;
  @Inject
  public VersioningInteractor versioningInteractor;
  @Inject
  public Config config;
  @Inject
  public ITracker tracker;

  public KeepAliveGASession keepAliveGASession;

  public Main() { }

  @Override
  public void start(Stage stage) throws Exception {
    Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());
//    if (isFirstLaunch()) {
      AppDirectory.initAppDir(UPDATE_DIR);
//      UpdateFX.restartApp();
//      return;
//    }

    sComponent = DaggerAppComponent.create();
    sComponent.inject(this);


    DEBUG = config.getDebug();
    Translation.getInstance(); // Initializing translations on first launch
    logger.info("Running in " + config);

    initLoggerSettings();
    settings.onAttach();
    keepAliveInteractor.onAttach();
    keepAliveInteractor.register(this);
    syncInteractor.onAttach();

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
    tracker.sendEvent(
        GAStatics.INSTANCE.getCATEGORY_BUTTON(),
        GAStatics.INSTANCE.getACTION_START()
    );
    keepAliveGASession = new KeepAliveGASessionImpl(
            logStorage,
            tracker,
            Schedulers.computation()
    );
    keepAliveGASession.onAttach();
    versioningInteractor.onAttach();
  }

  @Override
  public void stop() throws Exception {
    versioningInteractor.onDetach();
    keepAliveGASession.onDetach();
    syncInteractor.onDetach();
    keepAliveInteractor.unregister(this);
    keepAliveInteractor.onDetach();
    settings.onDetach();
    tracker.stop();
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
    fileAppenderProd = new RollingFileAppender(new PatternLayout(LOG_LAYOUT_PROD), config.getCfgPath() + "info_prod.log", true);
    fileAppenderProd.setMaxFileSize("100KB");
    fileAppenderProd.setMaxBackupIndex(1);
    fileAppenderProd.setThreshold(Priority.INFO);
    org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderProd);

    fileAppenderDebug = new RollingFileAppender(new PatternLayout(LOG_LAYOUT_DEBUG), config.getCfgPath() + "info.log", true);
    fileAppenderDebug.setMaxFileSize("1000KB");
    fileAppenderDebug.setMaxBackupIndex(1);
    fileAppenderDebug.setThreshold(Priority.INFO);
    org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderDebug);

    errorAppender = new RollingFileAppender(new PatternLayout(LOG_LAYOUT_DEBUG), config.getCfgPath() + "debug.log", true);
    errorAppender.setMaxFileSize("100000KB");
    errorAppender.setMaxBackupIndex(1);
    errorAppender.setThreshold(Priority.toPriority(Priority.ALL_INT));
    org.apache.log4j.Logger.getRootLogger().addAppender(errorAppender);
  }

  /**
   * Prepares an update directory
   * @deprecated not sure if need this at all
   */
  @Deprecated
  static boolean isFirstLaunch(boolean isDebug) throws IOException {
    if (isDebug) {
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

  @Override
  public void update() {
    if (autoUpdateInteractor.isAutoUpdateTimeoutHit(System.currentTimeMillis())) {
      syncInteractor.syncAll();
    }
  }

  //endregion

}
