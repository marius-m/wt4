package lt.markmerkk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.JiraConnector;
import lt.markmerkk.JiraLogFilterer;
import lt.markmerkk.JiraSearchJQL;
import lt.markmerkk.interfaces.IRemoteListener;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.RemoteFetchMerger;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by mariusmerkevicius on 1/5/16.
 * Handles synchronization with jira from other components
 */
public class SyncController {
  private static final Logger logger = LoggerFactory.getLogger(JiraSearchJQL.class);

  @Inject UserSettings settings;
  @Inject DBProdExecutor dbExecutor;
  @Inject BasicLogStorage storage;
//  @Inject WorkExecutor workExecutor;
  @Inject LastUpdateController lastUpdateController;

  //JiraLogExecutor jiraLogExecutor;
  List<IRemoteLoadListener> remoteLoadListeners = new ArrayList<>();
  JiraClient jiraClient;

  boolean loading = false;
  PublishSubject<WorkLog> publishSubject = PublishSubject.create();
  private Subscription subscription;

  @PostConstruct
  public void init() {
//    jiraLogExecutor = new JiraLogExecutor(remoteListener, remoteLoadListener);
//    jiraLogExecutor.onStart();
  }

  @PreDestroy
  public void destroy() {
    //jiraLogExecutor.onStop();
  }

  /**
   * Main method to start synchronization
   */
  public void sync() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
      remoteLoadListener.onLoadChange(false);
      logger.info("Cancelling sync!");
      return;
    }
    lastUpdateController.setError(false);
    DateTime startTime;
    DateTime endTime;
    switch (storage.getDisplayType()) {
      case WEEK:
        startTime = storage.getTargetDate().withDayOfWeek(DateTimeConstants.MONDAY);
        endTime = storage.getTargetDate().withDayOfWeek(DateTimeConstants.SUNDAY);
        break;
      default:
        startTime = storage.getTargetDate();
        endTime = storage.getTargetDate().plusDays(1);
    }

    // Forming jira client
    Observable.create(new JiraConnector(
        settings.getHost(),
        settings.getUsername(),
        settings.getPassword()
    )).subscribe(
        jiraClient -> SyncController.this.jiraClient = jiraClient,
        error -> logger.info(error.getMessage())
    );
    if (jiraClient == null)
      return;

    remoteLoadListener.onLoadChange(true);
    Observable<WorkLog> observable = renewWorklogsObservable(startTime, endTime);
    PublishSubject<WorkLog> publishSubject = PublishSubject.create();
    publishSubject.subscribe(
        workLog -> logger.info("Adding filtered worklog: " + workLog),
        error -> remoteLoadListener.onLoadChange(false),
        () -> remoteLoadListener.onLoadChange(false)
    );
    subscription = observable.subscribe(publishSubject);
  }

  //region Convenience

  /**
   * Pulls the {@link WorkLog}'s using {@link JiraClient}
   * @param startTime worklog start time
   * @param endTime worklog end time
   * @return
   */
  private Observable<WorkLog> renewWorklogsObservable(DateTime startTime, DateTime endTime) {
    return Observable.create(new JiraSearchJQL(jiraClient, startTime, endTime))
        .flatMap(searchResult -> Observable.from(searchResult.issues))
        .map(issue -> issue.getKey())
        .flatMap(key -> {
          try {
            return Observable.just(SyncController.this.jiraClient.getIssue(key));
          } catch (JiraException e) {
            return Observable.error(e);
          }
        })
        .filter(issue -> issue != null)
        .flatMap(issue -> {
          logger.info("Filtering logs for the " + issue.getKey());
          try {
            return Observable.from(issue.getAllWorkLogs());
          } catch (JiraException e) {
            return Observable.error(e);
          }
        })
        .flatMap(workLog -> {
          return Observable.create(new JiraLogFilterer(
              settings.getUsername(),
              startTime,
              endTime,
              workLog));
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(JavaFxScheduler.getInstance());
  }

  //endregion

  //region Getters / Setters

  public boolean isLoading() {
    return loading;
  }

  public void addLoadingListener(IRemoteLoadListener listener) {
    if (listener == null) return;
    remoteLoadListeners.add(listener);
  }

  public void removeLoadingListener(IRemoteLoadListener listener) {
    if (listener == null) return;
    remoteLoadListeners.remove(listener);
  }

  //endregion

  //region Listeners

  IRemoteListener remoteListener = new IRemoteListener() {

    @Override
    public void onWorklogDownloadComplete(Map<String, List<WorkLog>> remoteLogs) {
      for (String key : remoteLogs.keySet())
        for (WorkLog workLog : remoteLogs.get(key))
          new RemoteFetchMerger(dbExecutor, key, workLog).merge();
      Platform.runLater(() -> {
        storage.notifyDataChange();
      });
    }

    @Override
    public void onError(String error) {
      Platform.runLater(() -> {
        lastUpdateController.setError(true);
      });
    }

    @Override
    public void onCancel() {

    }
  };

  IRemoteLoadListener remoteLoadListener = new IRemoteLoadListener() {
    @Override
    public void onLoadChange(boolean loading) {
      lastUpdateController.setLoading(loading);
      if (!loading) {
        storage.notifyDataChange();
        lastUpdateController.refresh();
      }
      for (IRemoteLoadListener remoteListeners : SyncController.this.remoteLoadListeners)
        remoteListeners.onLoadChange(loading);
    }
  };

  //endregion

}
