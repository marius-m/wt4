package lt.markmerkk.utils;

import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import lt.markmerkk.Translation;
import lt.markmerkk.entities.LocalIssue;
import lt.markmerkk.entities.RemoteEntity;
import lt.markmerkk.entities.database.interfaces.IExecutor;
import lt.markmerkk.entities.jobs.DeleteJob;
import lt.markmerkk.entities.jobs.QueryListJob;
import lt.markmerkk.entities.jobs.RowCountJob;
import lt.markmerkk.UserSettings;
import lt.markmerkk.utils.abs.SearchableComboBoxDecorator;
import net.rcarz.jiraclient.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.JavaFxObservable;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by mariusmerkevicius on 2/3/16.
 * {@link Issue} searchable combo box.
 *
 * @deprecated - this class should be refactored as soon as possible for its overwhelming functionality.
 */
@Deprecated
public class IssueSearchAdapter extends SearchableComboBoxDecorator<LocalIssue> {
  public static final Logger logger = LoggerFactory.getLogger(IssueSearchAdapter.class);

  UserSettings settings;
//  SyncInteractor syncController;
  IExecutor dbExecutor;
  IssueSplit issueSplit = new IssueSplit();

  Text viewInfo; // Will hold some minor info below the search adapter

  Subscription refreshSubscription;

  long totalIssues;

  public IssueSearchAdapter(UserSettings settings,
                            ComboBox<LocalIssue> comboBox,
                            IExecutor executor,
                            Text viewInfo
  ) {
    super(comboBox);
    this.settings = settings;
    this.dbExecutor = executor;
    this.viewInfo = viewInfo;
    registerSearchObservable(comboBox);

    setTotalIssues(refreshTotalCount());
  }

//  /**
//   * Does a search with {@link ComboBox} input text
//   */
//  public void doRefresh() {
//    SimpleTracker.getInstance().getTracker().sendEvent(
//        SimpleTracker.CATEGORY_BUTTON,
//        SimpleTracker.ACTION_SEARCH_REFRESH
//    );
//    refreshCache();
//  }

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

  //region Getters / Setters

  public void setTotalIssues(long totalIssues) {
    this.totalIssues = totalIssues;
    Platform.runLater(() -> {
      viewInfo.setText(String.format(Translation.getInstance().getString("clock_jql_info"), totalIssues));
    });
  }


  //endregion

  //region Convenience

  /**
   * Returns total count of the issues in the database
   * @return
   */
  int refreshTotalCount() {
    RowCountJob<LocalIssue> issueCount = new RowCountJob<>(LocalIssue.class);
    dbExecutor.execute(issueCount);
    return issueCount.result();
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
//    if (refreshSubscription != null && !refreshSubscription.isUnsubscribed()) {
//      refreshSubscription.unsubscribe();
//      logger.debug("Cancelled!");
//      changeLoadState(false);
//      return;
//    }
//    changeLoadState(true);
//    long downloadMillis = System.currentTimeMillis();
//    refreshSubscription =
//        Observable.merge(searchIssues(settings.getIssueJql(), downloadMillis), clearOldCacheObservable(downloadMillis))
//            .observeOn(Schedulers.computation())
//            .subscribeOn(Schedulers.computation())
//            .subscribe(noResult -> {
//               Need cleanup of old issues
//            }, error -> {
//              logger.error("Error!", error);
//              changeLoadState(false);
//            }, () -> {
//              logger.debug("Complete!");
//              setTotalIssues(refreshTotalCount());
//              changeLoadState(false);
//            });
  }

  //endregion

  //region Listeners

  /**
   * Registers a search observable that queries local database for results
   * @param comboBox
   */
  // todo : convert to mvp
  void registerSearchObservable(ComboBox<LocalIssue> comboBox) { // todo : needs refactor
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
                            LocalIssue.KEY_DESCRIPTION, out.get(IssueSplit.Companion.getDESCRIPTION_KEY()),
                            LocalIssue.KEY_KEY, out.get(IssueSplit.Companion.getKEY_KEY()),
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

  //region Observables

  /**
   * Searches for issues from the remote
   * @return
   */
  Observable searchIssues(String issueJql, long downloadMillis) {
      // todo incomplete
    return Observable.empty();
//    RemoteFetchIssue fetchIssue = new RemoteFetchIssue(dbExecutor, downloadMillis);
//    return syncController.clientObservable()
//        .flatMap(jiraClient -> JiraObservables.userIssues(jiraClient, issueJql))
//        .flatMap(issue -> {
//          fetchIssue.merge(issue);
//          return Observable.empty();
//        });
  }

  /**
   * Clears out issues that have older download date
   * @param downloadMillis
   * @return
   */
  Observable clearOldCacheObservable(long downloadMillis) {
    return Observable.just(downloadMillis)
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
          logger.debug("Deleting old issue: " + oldIssue);
          DeleteJob deleteJob = new DeleteJob(LocalIssue.class, oldIssue);
          dbExecutor.execute(deleteJob);
          return Observable.just(oldIssue);
        });
  }

}
