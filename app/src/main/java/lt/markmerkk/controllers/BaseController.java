package lt.markmerkk.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.jobs.CreateJobIfNeeded;
import lt.markmerkk.utils.SimpleSettings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created with IntelliJ IDEA.
 * User: marius
 * Date: 10/23/13
 * Time: 8:47 PM
 */
public abstract class BaseController {
    protected final DBProdExecutor executor;
    protected Log log = LogFactory.getLog(MainController.class);
    private Appender guiAppender;
    protected SimpleSettings settings;

    public interface BaseControllerDelegate {
        Stage getStage();
        BaseController pushScene(String sceneXml, Object data);
        void popScene();
    }

    protected BaseControllerDelegate masterListener;
    protected Scene masterScene;

    abstract void onInternalOutput(String message);

    public BaseController() {
        settings = new SimpleSettings();
        guiAppender = new AppenderSkeleton() {

            @Override
            public boolean requiresLayout() { return true; }

            @Override
            public void close() { }

            @Override
            protected void append(LoggingEvent event) {
                onInternalOutput(layout.format(event));
            }
        };
        guiAppender.addFilter(new Filter() {
            @Override public int decide(LoggingEvent event) {
                return 0;
            }
        });
        guiAppender.setLayout(new PatternLayout("%d{ABSOLUTE} - %m%n"));

        // Initializing database
        executor = new DBProdExecutor();
        executor.execute(new CreateJobIfNeeded<>(SimpleLog.class));
    }

    public void setupController(BaseControllerDelegate listener, Scene scene, Stage primaryStage) {
        masterListener = listener;
        masterScene = scene;
    }

    public Scene getMasterScene() {
        return masterScene;
    }

    //region World events

    public void create(Object data) {
        settings.load();
        Logger.getRootLogger().addAppender(guiAppender);
    }

    public void destroy() {
        settings.save();
        Logger.getRootLogger().removeAppender(guiAppender);
        guiAppender.close();
    }

    public void resume() { }

    public void pause() { }

    //endregion
}
