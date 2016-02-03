package lt.markmerkk.utils;

import com.google.common.util.concurrent.Uninterruptibles;
import com.vinumeris.updatefx.AppDirectory;
import com.vinumeris.updatefx.Crypto;
import com.vinumeris.updatefx.UpdateFX;
import com.vinumeris.updatefx.UpdateSummary;
import com.vinumeris.updatefx.Updater;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lt.markmerkk.Main;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

/**
 * Created by mariusmerkevicius on 2/3/16.
 * Responsible of downloading and controlling app versioning.
 */
public class VersionController {
  public static final Logger logger = LoggerFactory.getLogger(VersionController.class);

  Subscription subscription;
  UpdateSummary summary;
  List<UpgradeListener> listeners = new ArrayList<>();

  /* Representes the updater state.
  -1 - not loading
  0 - 100 - load percent.
  */
  double progress = 0;

  @PostConstruct
  public void init() {
    // On init we check if we have any updates
    checkForUpdate();
  }

  @PreDestroy
  public void destroy() {
    if (subscription != null && !subscription.isUnsubscribed())
      subscription.unsubscribe();
  }

  public void checkForUpdate() {
    if (subscription != null && !subscription.isUnsubscribed())
      return;
    logger.info("Looking for updates...");
    subscription = updateObservable
        .observeOn(JavaFxScheduler.getInstance())
        .subscribe(updateSummary -> {
          onSummaryChange(updateSummary);
        }, error -> {
          logger.error("Error: ", error);
          onProgressChange(-1);
        }, () -> {
          onProgressChange(1.0f);
        });
  }

  /**
   * Does the app upgrade
   */
  public void upgrade() {
    if (summary == null) {
      logger.error("Error upgrading app!");
      return;
    }
    if (summary.highestVersion <= Main.VERSION) {
      logger.error("App is up to date!");
      return;
    }
    UpdateFX.pinToVersion(AppDirectory.dir(), summary.highestVersion);
    UpdateFX.restartApp();
  }

  /**
   * Inner function to change summary
   * @param updateSummary
   */
  void onSummaryChange(UpdateSummary updateSummary) {
    this.summary = updateSummary;
    Platform.runLater(() -> {
      for (UpgradeListener listener : listeners)
        listener.onSummaryUpdate(updateSummary);
    });
  }

  /**
   * Inner function to change progress and report the changes
   * for all listeners
   * @param progress new progress indication
   */
  void onProgressChange(double progress) {
    this.progress = progress;
    Platform.runLater(() -> {
      for (UpgradeListener listener : listeners)
        listener.onProgressChange(progress);
    });
  }

  //region Getters / Setters

  public double getProgress() {
    return progress;
  }

  public UpdateSummary getSummary() {
    return summary;
  }

  public void addListener(UpgradeListener listener) {
    listeners.add(listener);
  }

  public void removeListener(UpgradeListener listener) {
    listeners.remove(listener);
  }

  //endregion

  //region Classes

  /**
   * Helper listener that calls back event changes
   */
  public interface UpgradeListener {
    /**
     * Reports progress changes
     * @param progressChange
     */
    void onProgressChange(double progressChange);

    /**
     * Reports update summary
     * @param updateSummary
     */
    void onSummaryUpdate(UpdateSummary updateSummary);
  }

  //endregion

  //region Observables

  /**
   * An observable that is responsible for checking if a new version exists and downloads it
   */
  public Observable<UpdateSummary> updateObservable = Observable.create(new Observable.OnSubscribe<Updater>() {
    @Override
    public void call(Subscriber<? super Updater> subscriber) {
      System.out.println("Code path: " + UpdateFX.findCodePath(Main.class));
      List<ECPoint> pubkeys = Crypto.decode("0335FE0506672CAD82FFDD7AEBF61EC5DE312507835D930D53F0345EFC8471FB72");
      Updater updater = new Updater(URI.create("http://localhost:80/index"), "" + Main.VERSION,
          AppDirectory.dir(), UpdateFX.findCodePath(Main.class), pubkeys, 1) {
        @Override
        protected void updateProgress(long workDone, long max) {
          super.updateProgress(workDone, max);
          float progress = workDone * 100 / max;
          final float finalProgress = ((float) progress) / 100;
          onProgressChange(finalProgress);
          Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        }
      };
      subscriber.onNext(updater);
      subscriber.onCompleted();
    }
  }).observeOn(Schedulers.computation())
      .map(updater -> {
        onProgressChange(0.01f);
        return updater;
      })
      .flatMap(updater -> {
        return Observable.create(new Observable.OnSubscribe<UpdateSummary>() {
          @Override
          public void call(Subscriber<? super UpdateSummary> subscriber) {
            updater.setOnSucceeded(event -> {
              try {
                UpdateSummary summary = updater.get();
                subscriber.onNext(summary);
                subscriber.onCompleted();
              } catch (InterruptedException e) {
                subscriber.onError(e);
              } catch (ExecutionException e) {
                subscriber.onError(e);
              }
            });
            updater.setOnFailed(event -> {
              subscriber.onError(updater.getException());
            });
            // Todo : remove time mock
            try {
              //Thread.sleep(2000);
              for(float i = 0.0f; i < 1.0f; i += 0.05f) {
                Thread.sleep(30);
                onProgressChange(i);
              }
            } catch (InterruptedException e) { }
            updater.run();
          }
        });
      });

  //endregion

}
