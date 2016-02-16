package lt.markmerkk.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import lt.markmerkk.JiraObservables;
import lt.markmerkk.JiraSearchJQL;
import lt.markmerkk.utils.abs.SearchableComboBoxDecorator;
import net.rcarz.jiraclient.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

/**
 * Created by mariusmerkevicius on 2/3/16.
 * {@link Issue} searchable combo box.
 */
public class IssueSearchAdapter extends SearchableComboBoxDecorator<Issue> {
  public static final Logger logger = LoggerFactory.getLogger(IssueSearchAdapter.class);

  SyncController syncController;
  Subscription searchSubscription;

  public IssueSearchAdapter(SyncController controller,
                            ComboBox<Issue> comboBox,
                            ProgressIndicator progressIndicator) {
    super(comboBox, progressIndicator);
    this.syncController = controller;
  }

  /**
   * Does a search with {@link ComboBox} input text
   */
  public void doSearch() {
//    doSearchForInput(comboBox.getEditor().getText());
    refreshCache();
  }

  //region Abs implementation

  @Override
  protected void onKeyEvent(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case ENTER:
//        doSearchForInput(comboBox.getEditor().getText());
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
  protected StringConverter<Issue> converter() {
    return new StringConverter<Issue>() {
      @Override
      public String toString(Issue object) {
        if (object == null) return "";
        return object.getKey() + " : " + object.getSummary() + " / " + object.getAssignee();
      }

      @Override
      public Issue fromString(String string) {
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
   * Traverses current jira issues for user issues
   */
  public void refreshCache() {
    if (searchSubscription != null && !searchSubscription.isUnsubscribed()) {
      searchSubscription.unsubscribe();
      logger.debug("Cancelled!");
      changeLoadState(false);
      return;
    }
    changeLoadState(true);
    searchSubscription =
        syncController.clientObservable()
            .flatMap(jiraClient -> JiraObservables.userIssues(jiraClient, JiraSearchJQL.DEFAULT_JQL_USER_ISSUES))
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .flatMap(issue -> {
              return Observable.empty();
            })
            .subscribe(noResult -> {
            }, error -> {
              logger.error("Error!", error);
              changeLoadState(false);
            }, () -> {
              logger.debug("Complete!");
              changeLoadState(false);
            });
  }

//  /**
//   * Does a search with custom text
//   * @param input
//   */
//  public void doSearchForInput(String input) {
//    if (searchSubscription != null && searchSubscription.isUnsubscribed())
//      searchSubscription.unsubscribe();
//    searchSubscription = Observable.just(input)
//        .filter(searchPhrase -> !Strings.isNullOrEmpty(searchPhrase))
//        .observeOn(JavaFxScheduler.getInstance())
//        .map(sp -> {
//          loadProgressIndicator.setManaged(true);
//          loadProgressIndicator.setVisible(true);
//          comboBox.hide();
//          return sp;
//        })
//        .observeOn(Schedulers.computation())
//        .flatMap(searchPhrase -> ClientObservables.issueSearchInputObservable(searchPhrase))
//        .flatMap(jql -> {
//          logger.info("Searching for \"" + jql + "\"");
//          return Observable.create(new JiraSearchJQL(syncController.getJiraClient(), jql));
//        })
//        .flatMap(searchResult -> {
//          syncController.reinitJiraClient();
//          logger.info("Search result: " + searchResult.issues.size());
//          if (searchResult.issues.size() == 0)
//            return Observable.empty();
//          return Observable.just(FXCollections.observableArrayList(searchResult.issues));
//        })
//        .observeOn(JavaFxScheduler.getInstance())
//        .subscribe(issues -> {
//          comboBox.setItems(issues);
//          comboBox.show();
//        }, error -> {
//          logger.error("Error doing search. " + error);
//          loadProgressIndicator.setManaged(false);
//          loadProgressIndicator.setVisible(false);
//        }, () -> {
//          loadProgressIndicator.setManaged(false);
//          loadProgressIndicator.setVisible(false);
//        });
//  }

  //endregion

}
