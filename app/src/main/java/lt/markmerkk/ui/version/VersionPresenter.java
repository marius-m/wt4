package lt.markmerkk.ui.version;

import com.google.common.util.concurrent.Uninterruptibles;
import com.vinumeris.updatefx.AppDirectory;
import com.vinumeris.updatefx.Crypto;
import com.vinumeris.updatefx.UpdateFX;
import com.vinumeris.updatefx.UpdateSummary;
import com.vinumeris.updatefx.Updater;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import lt.markmerkk.Main;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.ui.interfaces.DialogListener;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

/**
 * Created by mariusmerkevicius on 12/14/15. Represents the presenter to update the log
 */
public class VersionPresenter implements Initializable, Destroyable {
  Logger logger = LoggerFactory.getLogger(VersionPresenter.class);

  @FXML Button buttonClose;
  @FXML Hyperlink buttonTitle;
  @FXML Hyperlink buttonAuthor;
  @FXML Hyperlink buttonPlace;
  @FXML Hyperlink buttonUpdate;
  @FXML ProgressIndicator progressIndicator;

  DialogListener dialogListener;
  private Subscription subscription;

  @Override
  public void initialize(URL location, ResourceBundle resources) { }

  @Override
  public void destroy() {
    if (subscription != null && !subscription.isUnsubscribed())
      subscription.unsubscribe();
  }

  public void onClickClose() {
    destroy();
    if (dialogListener == null) return;
    dialogListener.onCancel();
  }

  public void onClickSave() { }

  public void onClickTitle() {
    if (Main.hostServices != null)
      Main.hostServices.showDocument("https://bitbucket.org/mmerkevicius/wt4");
  }

  public void onClickAuthor() {
    if (Main.hostServices != null)
      Main.hostServices.showDocument("https://github.com/marius-m");
  }

  public void onClickPlace() {
    if (Main.hostServices != null)
      Main.hostServices.showDocument("http://ito.lt");
  }

  public void onClickUpdate() {
    if (subscription != null && !subscription.isUnsubscribed())
      return;
    logger.info("Looking for updates...");
    Observable<UpdateSummary> updateObservable = Observable.create(new Observable.OnSubscribe<UpdateSummary>() {
      @Override
      public void call(Subscriber<? super UpdateSummary> subscriber) {
        List<ECPoint> pubkeys = Crypto.decode("0335FE0506672CAD82FFDD7AEBF61EC5DE312507835D930D53F0345EFC8471FB72");
        Updater updater = new Updater(URI.create("http://localhost:80/index"), "" + Main.VERSION,
            AppDirectory.dir(), UpdateFX.findCodePath(Main.class), pubkeys, 1) {
          @Override
          protected void updateProgress(long workDone, long max) {
            super.updateProgress(workDone, max);
            //progressIndicator.setProgress(workDone);
            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
          }
        };
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
        Thread asyncThread = new Thread(updater);
        asyncThread.run();
        subscriber.add(new Subscription() {
          @Override
          public void unsubscribe() {
            if (asyncThread.isAlive())
              asyncThread.interrupt();
          }

          @Override
          public boolean isUnsubscribed() {
            return false;
          }
        });
      }
    });

    //      if (updateSummary.descriptions.size() > 0) {
//        logger.info("One liner: {}", updateSummary.descriptions.get(0).getOneLiner());
//        logger.info("{}", updateSummary.descriptions.get(0).getDescription());
//      }
//        logger.info("Restarting to get version " + summary.highestVersion);
//        UpdateFX.pinToVersion(AppDirectory.dir(), summary.highestVersion);
//        UpdateFX.restartApp();
    subscription = updateObservable.subscribeOn(Schedulers.io())
        .observeOn(JavaFxScheduler.getInstance())
        .subscribe(updateSummary -> {
//      if (updateSummary.descriptions.size() > 0) {
//        logger.info("One liner: {}", updateSummary.descriptions.get(0).getOneLiner());
//        logger.info("{}", updateSummary.descriptions.get(0).getDescription());
//      }
          if (updateSummary.highestVersion > Main.VERSION) {
            logger.info("New version is available!");
//        logger.info("Restarting to get version " + summary.highestVersion);
//        UpdateFX.pinToVersion(AppDirectory.dir(), summary.highestVersion);
//        UpdateFX.restartApp();
          } else {
            logger.info("App is up to date!");
          }
        }, error -> {
          logger.info("Error: " + error.getMessage());
        }, () -> {
          logger.info("Complete!");
        });
  }

  //region Listeners

  public void setDialogListener(DialogListener dialogListener) {
    this.dialogListener = dialogListener;
  }

  //endregion

  //region Classes

  public interface UpdateListenerAdapter {
    void onSuccess();
  }

  //endregion

}
