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
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.RemoteFetchMerger;
import lt.markmerkk.storage2.RemotePushMerger;
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

    // Forming jira client
    Observable.create(new JiraConnector(
        settings.getHost(),
        settings.getUsername(),
        settings.getPassword()
    )).subscribe(
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

    RemoteFetchMerger remoteFetchMerger = new RemoteFetchMerger(dbExecutor);
    RemotePushMerger remotePushMerger = new RemotePushMerger(dbExecutor, jiraClient);

    Observable<String> downloadObservable =
        JiraObservables.remoteWorklogs(jiraClient, startTime, endTime, filterer)
            .map(pair -> {
              for (WorkLog workLog : pair.getValue())
                remoteFetchMerger.merge(pair.getKey().getKey(), workLog);
              return null;
            });

    Observable<String> uploadObservable = Observable.from(storage.getData())
        .map(simpleLog -> {
          remotePushMerger.merge(simpleLog);
          return null;
        });

    remoteLoadListener.onLoadChange(true);

    subscription = uploadObservable
        .subscribeOn(Schedulers.computation())
        .observeOn(JavaFxScheduler.getInstance())
        .subscribe(output -> {
            },
            error -> {
              logger.info("Upload error!  " + error);
              remoteLoadListener.onLoadChange(false);
              remoteLoadListener.onError(error.getMessage());
            }, () -> {
              logger.info("Upload complete! ");
              //remoteLoadListener.onLoadChange(false);
              storage.notifyDataChange();

              downloadObservable
                  .subscribeOn(Schedulers.computation())
                  .observeOn(JavaFxScheduler.getInstance())
                  .subscribe(output -> {
                      },
                      error -> {
                        logger.info("Download error! " + error);
                        remoteLoadListener.onError(error.getMessage());
                      }, () -> {
                        logger.info("Download complete! ");
                        remoteLoadListener.onLoadChange(false);
                        storage.notifyDataChange();
                      });
            });
  }

  //region Convenience

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

}
