package lt.markmerkk.utils;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.JiraConnector;
import lt.markmerkk.JiraLogFilterer;
import lt.markmerkk.JiraObservables;
import lt.markmerkk.JiraSearchJQL;
import lt.markmerkk.events.StartIssueSyncEvent;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.RemoteLogFetchMerger;
import lt.markmerkk.storage2.RemoteLogPushMerger;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

/**
 * Created by mariusmerkevicius on 1/5/16. Handles synchronization with jira from other components
 */
public class SyncController {
  private static final Logger logger = LoggerFactory.getLogger(JiraSearchJQL.class);

  @Inject
  UserSettings settings;
  @Inject
  DBProdExecutor dbExecutor;
  @Inject
  BasicLogStorage storage;
  @Inject
  LastUpdateController lastUpdateController;

  List<IRemoteLoadListener> remoteLoadListeners = new ArrayList<>();
  Subscription subscription;
  JiraClient jiraClient;

  boolean loading = false;

  @PostConstruct
  public void init() {
    clientObservable();
  }

  @PreDestroy
  public void destroy() {
    if (subscription != null && !subscription.isUnsubscribed())
      subscription.unsubscribe();
  }

  /**
   * Main method to start synchronization
   */
  public void sync() {
    // Cancel check
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
      remoteLoadListener.onLoadChange(false);
      logger.info("Cancelling sync!");
      return;
    }

    // Data prepare
    DateTime startTime;
    DateTime endTime;
    switch (storage.getDisplayType()) {
      case WEEK:
        startTime = storage.getTargetDate()
            .withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        endTime = storage.getTargetDate()
            .withDayOfWeek(DateTimeConstants.SUNDAY)
            .plusDays(1).withTimeAtStartOfDay();
        break;
      default:
        startTime = storage.getTargetDate();
        endTime = storage.getTargetDate().plusDays(1);
    }

    clientObservable().subscribe(
        jiraClient -> SyncController.this.jiraClient = jiraClient,
        error -> {
          logger.info(error.getMessage());
          remoteLoadListener.onError(error.getMessage());
        }
    );

    if (jiraClient == null)
      return;

    JiraLogFilterer filterer = new JiraLogFilterer(
        settings.getUsername(),
        startTime,
        endTime
    );

    RemoteLogFetchMerger remoteLogFetchMerger = new RemoteLogFetchMerger(dbExecutor);
    RemoteLogPushMerger remoteLogPushMerger = new RemoteLogPushMerger(dbExecutor, jiraClient);

    Observable<String> downloadObservable =
        JiraObservables.remoteWorklogs(jiraClient, filterer, startTime, endTime)
            .map(pair -> {
              for (WorkLog workLog : pair.getValue())
                remoteLogFetchMerger.merge(pair.getKey().getKey(), workLog);
              return null;
            });

    Observable<String> uploadObservable = Observable.from(storage.getData())
        .map(simpleLog -> {
          remoteLogPushMerger.merge(simpleLog);
          return null;
        });

    remoteLoadListener.onLoadChange(true);
    subscription = downloadObservable.startWith(uploadObservable)
        .subscribeOn(Schedulers.computation())
        .observeOn(JavaFxScheduler.getInstance())
        .subscribe(output -> {
              //logger.info(output);
            },
            error -> {
              logger.info("Sync error!  " + error);
              remoteLoadListener.onLoadChange(false);
              remoteLoadListener.onError(error.getMessage());
            }, () -> {
              logger.info("Sync complete! ");
              remoteLoadListener.onLoadChange(false);
              storage.notifyDataChange();
              SyncEventBus.getInstance().getEventBus().post(new StartIssueSyncEvent());
            });
  }

  //region Convenience


  //endregion

  //region Getters / Setters

  public JiraClient getJiraClient() {
    return jiraClient;
  }

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

  IRemoteLoadListener remoteLoadListener = new IRemoteLoadListener() {
    @Override
    public void onLoadChange(boolean loading) {
      SyncController.this.loading = loading;
      if (loading)
        lastUpdateController.setError(null);
      if (!loading)
        lastUpdateController.refresh();
      lastUpdateController.setLoading(loading);
      for (IRemoteLoadListener remoteListeners : SyncController.this.remoteLoadListeners)
        remoteListeners.onLoadChange(loading);
    }

    @Override
    public void onError(String error) {
      lastUpdateController.setError(error);
      for (IRemoteLoadListener remoteListeners : SyncController.this.remoteLoadListeners)
        remoteListeners.onError(error);
    }
  };

  //endregion

  //region Observables

  /**
   * Returns an observable for jira client initialization
   */
  public Observable<JiraClient> clientObservable() {
    // Forming jira client
    return Observable.create(new JiraConnector(
        settings.getHost(),
        settings.getUsername(),
        settings.getPassword()
    ));
  }

  //endregion


}
