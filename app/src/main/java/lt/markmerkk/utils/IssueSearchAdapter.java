package lt.markmerkk.utils;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import lt.markmerkk.JiraObservables;
import lt.markmerkk.events.StartAllSyncEvent;
import lt.markmerkk.events.StartIssueSyncEvent;
import lt.markmerkk.events.StartLogSyncEvent;
import lt.markmerkk.storage2.IssueSplit;
import lt.markmerkk.storage2.LocalIssue;
import lt.markmerkk.storage2.RemoteEntity;
import lt.markmerkk.storage2.RemoteFetchIssue;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.QueryListJob;
import lt.markmerkk.utils.abs.SearchableComboBoxDecorator;
import lt.markmerkk.utils.tracker.SimpleTracker;
import net.rcarz.jiraclient.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.JavaFxObservable;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

/**
 * Created by mariusmerkevicius on 2/3/16.
 * {@link Issue} searchable combo box.
 */
public class IssueSearchAdapter extends SearchableComboBoxDecorator<LocalIssue> {
  public static final Logger logger = LoggerFactory.getLogger(IssueSearchAdapter.class);

  UserSettings settings;
  SyncController syncController;
  IExecutor dbExecutor;
  IssueSplit issueSplit = new IssueSplit();

  Subscription refreshSubscription;

  ObservableList<LocalIssue> issues;

  public IssueSearchAdapter(UserSettings settings, SyncController controller,
                            ComboBox<LocalIssue> comboBox,
                            ProgressIndicator progressIndicator,
                            IExecutor executor) {
    super(comboBox, progressIndicator);
    this.settings = settings;
    this.syncController = controller;
    this.dbExecutor = executor;
    registerSearchObservable(comboBox);

    SyncEventBus.getInstance().getEventBus().register(this);
  }

  /**
   * Does a search with {@link ComboBox} input text
   */
  public void doRefresh() {
    SimpleTracker.getInstance().getTracker().sendEvent(
        SimpleTracker.CATEGORY_BUTTON,
        SimpleTracker.ACTION_SEARCH_REFRESH
    );
    refreshCache();
  }

  //region Events

  @Subscribe
  public void onEvent(StartIssueSyncEvent event) {
    if (refreshSubscription != null && !refreshSubscription.isUnsubscribed()) {
      refreshSubscription.unsubscribe();
      return;
    }
    doRefresh();
  }

  @Subscribe
  public void onEvent(StartAllSyncEvent event) {
    if (refreshSubscription != null && !refreshSubscription.isUnsubscribed()) {
      refreshSubscription.unsubscribe();
      return;
    }
    doRefresh();
  }

  //endregion

  //region Abs implementation

  @Override
  protected void onKeyEvent(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case ENTER:
        keyEvent.consume();
        break;
      case DOWN:
      case UP:
        comboBox.show();
        keyEvent.consume();
        break;
    }

  }

  @Override
  protected StringConverter<LocalIssue> converter() {
    return new StringConverter<LocalIssue>() {
      @Override
      public String toString(LocalIssue object) {
        if (object == null) return "";
        return object.getKey() + " : " + object.getDescription();
      }

      @Override
      public LocalIssue fromString(String string) {
        return comboBox.getSelectionModel().getSelectedItem();
      }
    };
  }

  //endregion

  //region Convenience

  /**
   * Clears out old database cache
   */
  void clearOldCache(long downloadMillis) {
    Observable.just(downloadMillis)
        .subscribeOn(Schedulers.computation())
        .flatMap(download -> {
          return Observable.create(new Observable.OnSubscribe<LocalIssue>() {
            @Override
            public void call(Subscriber<? super LocalIssue> subscriber) {
              try {
                QueryListJob<LocalIssue> issueQueryListJob =
                    new QueryListJob<LocalIssue>(LocalIssue.class, () -> {
                      return String.format("%s < %d", RemoteEntity.KEY_DOWNLOAD_MILLIS, download);
                    });
                dbExecutor.executeOrThrow(issueQueryListJob);
                for (LocalIssue localIssue : issueQueryListJob.result())
                  subscriber.onNext(localIssue);
                subscriber.onCompleted();
              } catch (ClassNotFoundException e) {
                subscriber.onError(e);
              } catch (SQLException e) {
                 subscriber.onError(e);
              }
            }
          });
        })
        .flatMap(oldIssue -> {
          logger.debug("Deleting old issue: "+oldIssue);
          DeleteJob deleteJob = new DeleteJob(LocalIssue.class, oldIssue);
          dbExecutor.execute(deleteJob);
          return Observable.just(oldIssue);
        }).subscribe();
  }

  /**
   * Changes loading state on the JavaFX thread
   * @param loading
   */
  void changeLoadState(boolean loading) {
    Observable.just(loading)
        .subscribeOn(JavaFxScheduler.getInstance())
        .subscribe(loadingState -> {
          loadProgressIndicator.setManaged(loadingState);
          loadProgressIndicator.setVisible(loadingState);
        });
  }

  /**
   * Changes data on the combo box on the JavaFX thread
   */
  void notifyDateChange(ObservableList<LocalIssue> issues) {
    Observable.just(issues)
        .subscribeOn(JavaFxScheduler.getInstance())
        .subscribe(localIssues -> {
          comboBox.setItems(localIssues);
          if (issues.size() > 0)
            comboBox.show();
          else
            comboBox.hide();
        });
  }

  /**
   * Traverses current jira issues for user issues
   */
  public void refreshCache() {
    if (refreshSubscription != null && !refreshSubscription.isUnsubscribed()) {
      refreshSubscription.unsubscribe();
      logger.debug("Cancelled!");
      changeLoadState(false);
      return;
    }
    changeLoadState(true);
    RemoteFetchIssue fetchIssue = new RemoteFetchIssue(dbExecutor, System.currentTimeMillis());
    refreshSubscription =
        syncController.clientObservable()
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .flatMap(jiraClient -> JiraObservables.userIssues(jiraClient, settings.getIssueJql()))
            .flatMap(issue -> {
              fetchIssue.merge(issue);
              return Observable.empty();
            })
            .subscribe(noResult -> {
              // Need cleanup of old issues
            }, error -> {
              logger.error("Error!", error);
              changeLoadState(false);
            }, () -> {
              logger.debug("Complete!");
              clearOldCache(fetchIssue.getDownloadMillis());
              changeLoadState(false);
            });
  }

  //endregion

  //region Listeners

  /**
   * Registers a search observable that queries local database for results
   * @param comboBox
   */
  void registerSearchObservable(ComboBox<LocalIssue> comboBox) {
    JavaFxObservable.fromObservableValue(comboBox.getEditor().textProperty())
        .filter(phrase -> (comboBox.getSelectionModel().getSelectedItem() == null))
        .filter(phrase -> !Strings.isNullOrEmpty(phrase))
        .debounce(200, TimeUnit.MILLISECONDS)
        .flatMap(phrase -> {
          return Observable.create(new Observable.OnSubscribe<List<LocalIssue>>() {
            @Override
            public void call(Subscriber<? super List<LocalIssue>> subscriber) {
              try {
                Map<String, String> out = issueSplit.split(phrase);
                QueryListJob<LocalIssue> queryListJob =
                    new QueryListJob<LocalIssue>(LocalIssue.class,
                        () -> String.format("(%s like '%%%s%%' OR %s like '%%%s%%') ORDER BY %s DESC",
                            LocalIssue.KEY_DESCRIPTION, out.get(IssueSplit.DESCRIPTION_KEY),
                            LocalIssue.KEY_KEY, out.get(IssueSplit.KEY_KEY),
                            LocalIssue.KEY_CREATE_DATE)
                    );
                dbExecutor.executeOrThrow(queryListJob);
                subscriber.onNext(queryListJob.result());
                subscriber.onCompleted();
              } catch (ClassNotFoundException e) {
                subscriber.onError(e);
              } catch (SQLException e) {
                subscriber.onError(e);
              }
            }
          });
        })
        .subscribe(localIssues -> {
          notifyDateChange(FXCollections.observableArrayList(localIssues));
        }, error -> {
          System.out.println("Error:  " + error);
        });
  }

  //endregion

}
