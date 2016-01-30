package lt.markmerkk.utils;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.util.Pair;
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
import lt.markmerkk.storage2.SimpleLog;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by mariusmerkevicius on 1/5/16.
 * Handles synchronization with jira from other components
 */
public class SyncController {
  private static final Logger logger = LoggerFactory.getLogger(JiraSearchJQL.class);

  @Inject UserSettings settings;
  @Inject DBProdExecutor dbExecutor;
  @Inject BasicLogStorage storage;
  @Inject LastUpdateController lastUpdateController;

  List<IRemoteLoadListener> remoteLoadListeners = new ArrayList<>();
  JiraClient jiraClient;

  boolean loading = false;
  private Subscription subscription;

  @PostConstruct
  public void init() { }

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
    JiraLogFilterer filterer = new JiraLogFilterer(
        settings.getUsername(),
        startTime,
        endTime
    );
    RemoteFetchMerger remoteFetchMerger = new RemoteFetchMerger(dbExecutor);
    RemotePushMerger remotePushMerger = new RemotePushMerger(dbExecutor, jiraClient);

    if (jiraClient == null)
      return;

    Observable<String> downloadObservable =
        JiraObservables.remoteWorklogs(jiraClient, startTime, endTime, filterer)
            .subscribeOn(Schedulers.computation())
            .observeOn(JavaFxScheduler.getInstance())
            .map(pair -> {
              for (WorkLog workLog : pair.getValue())
                remoteFetchMerger.merge(pair.getKey().getKey(), workLog);
              return "Remote worklogs: " + pair;
            });

    Observable<String> uploadObservable = Observable.from(storage.getData())
        .subscribeOn(Schedulers.computation())
        .observeOn(JavaFxScheduler.getInstance())
        .map(simpleLog -> {
          remotePushMerger.merge(simpleLog);
          return null;
        });

    remoteLoadListener.onLoadChange(true);

    subscription = uploadObservable
        .subscribe(output -> logger.info(output),
            error -> {
              logger.info("Upload error!  "+error);
              remoteLoadListener.onLoadChange(false);
            }, () -> {
              logger.info("Upload complete! ");
              //remoteLoadListener.onLoadChange(false);
              storage.notifyDataChange();

              downloadObservable
                  .subscribe(output -> logger.info(output),
                      error -> {
                        logger.info("Download error! "+error);
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

//  IRemoteListener remoteListener = new IRemoteListener() {
//
//    @Override
//    public void onWorklogDownloadComplete(Map<String, List<WorkLog>> remoteLogs) {
//      for (String key : remoteLogs.keySet())
//        for (WorkLog workLog : remoteLogs.get(key))
//          new RemoteFetchMerger(dbExecutor).merge();
//      Platform.runLater(() -> {
//        storage.notifyDataChange();
//      });
//    }
//
//    @Override
//    public void onError(String error) {
//      Platform.runLater(() -> {
//        lastUpdateController.setError(true);
//      });
//    }
//
//    @Override
//    public void onCancel() {
//
//    }
//  };

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
