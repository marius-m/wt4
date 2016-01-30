package lt.markmerkk;

import java.util.Iterator;
import java.util.List;
import javafx.util.Pair;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by mariusmerkevicius on 1/30/16.
 * A static class that forms component observables
 */
public class JiraObservables {

  /**
   * Returns an observable with {@link Issue} and its filtered {@link WorkLog}'s
   * @param client
   * @param start
   * @param end
   * @param filterer
   * @return
   */
  public static Observable<Pair<Issue, List<WorkLog>>> remoteWorklogs(
      JiraClient client, DateTime start, DateTime end, JiraLogFilterer filterer) {
    return Observable.create(new JiraSearchJQL(client, start, end))
        .flatMap(searchResult -> Observable.from(searchResult.issues))
        .map(issue -> issue.getKey())
        .flatMap(key -> {
          try {
            return Observable.just(client.getIssue(key));
          } catch (JiraException e) {
            return Observable.error(e);
          }
        })
        .filter(issue -> issue != null)
        .flatMap(
            new Func1<Issue, Observable<List<WorkLog>>>() {
              @Override
              public Observable<List<WorkLog>> call(Issue issue) {
                try {
                  return Observable.just(issue.getAllWorkLogs());
                } catch (JiraException e) {
                  return Observable.error(e);
                }
              }
            },
            new Func2<Issue, List<WorkLog>, Pair<Issue, List<WorkLog>>>() {
              @Override
              public Pair<Issue, List<WorkLog>> call(Issue issue, List<WorkLog> workLogs) {
                for (Iterator<WorkLog> logIterator = workLogs.iterator(); logIterator.hasNext(); )
                  if (filterer.filterLog(logIterator.next()) == null)
                    logIterator.remove();
                return new Pair<>(issue, workLogs);
              }
            }
        );
  }

  /**
   * Returns an observable that is responsible for pushing new {@link WorkLog}
   * @param client
   * @return
   */
  public static Observable<Boolean> newWorklogs(
      JiraClient client) {
    return null;
  }
}
