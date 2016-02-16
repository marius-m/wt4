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
import lt.markmerkk.JiraSearchJQL;
import lt.markmerkk.events.StartSyncEvent;
import lt.markmerkk.storage2.IssueSplit;
import lt.markmerkk.storage2.LocalIssue;
import lt.markmerkk.storage2.RemoteFetchIssue;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.QueryListJob;
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

/**
 * Created by mariusmerkevicius on 2/3/16.
 * {@link Issue} searchable combo box.
 */
public class IssueSearchAdapter extends SearchableComboBoxDecorator<LocalIssue> {
  public static final Logger logger = LoggerFactory.getLogger(IssueSearchAdapter.class);

  SyncController syncController;
  IExecutor dbExecutor;
  IssueSplit issueSplit = new IssueSplit();

  Subscription refreshSubscription;

  ObservableList<LocalIssue> issues;

  public IssueSearchAdapter(SyncController controller,
                            ComboBox<LocalIssue> comboBox,
                            ProgressIndicator progressIndicator,
                            IExecutor executor) {
    super(comboBox, progressIndicator);
    this.syncController = controller;
    this.dbExecutor = executor;
    registerSearchObservable(comboBox);

    SyncEventBus.getInstance().getEventBus().register(this);
  }

  /**
   * Does a search with {@link ComboBox} input text
   */
  public void doRefresh() {
    refreshCache();
  }

  //region Events

  @Subscribe
  public void onEvent(StartSyncEvent startSyncEvent) {
    doRefresh();
  }

  //endregion

  //region Abs implementation

  @Override
  protected void onKeyEvent(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case BACK_SPACE:
        comboBox.getSelectionModel().clearSelection();
        break;
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
            .flatMap(jiraClient -> JiraObservables.userIssues(jiraClient, JiraSearchJQL.DEFAULT_JQL_USER_ISSUES))
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
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
