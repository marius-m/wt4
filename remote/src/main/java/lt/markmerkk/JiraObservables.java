package lt.markmerkk;

import javafx.util.Pair;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import rx.Observable;

import java.util.List;

/**
 * Created by mariusmerkevicius on 1/30/16.
 * A static class that forms component observables
 */
@Deprecated // Use JiraObservables2 instead
public class JiraObservables {

  /**
   * Returns an observable with {@link Issue} and its filtered {@link WorkLog}'s
   * @param client
   * @param start
   * @param end
   * @param filterer
   * @return
   */
  @Deprecated
  public static Observable<Pair<Issue, List<WorkLog>>> remoteWorklogs(
          JiraClient client, JiraFilterWorklog filterer, DateTime start, DateTime end) {
    return Observable.empty();
//    return JiraObservables.issueSearchDateRangeObservable(start, end, client.getSelf())
//        .flatMap(jql -> Observable.create(
//                new JiraSearchSubscriberImpl(
//                        new JiraClientProviderImpl()
//                )
//        )
////        .flatMap(jql -> Observable.create(new JiraSearchSubscriberImpl(client, jql, "*navigable")))
//        .flatMap(searchResult -> {
//          return Observable.from(searchResult.issues);
//        })
//        .map(issue -> issue.getKey())
//        .flatMap(key -> {
//          try {
//            return Observable.just(client.getIssue(key));
//          } catch (JiraException e) {
//            return Observable.error(e);
//          }
//        })
//        .filter(issue -> issue != null)
//        .flatMap(
//            new Func1<Issue, Observable<List<WorkLog>>>() {
//              @Override
//              public Observable<List<WorkLog>> call(Issue issue) {
//                try {
//                  return Observable.just(issue.getAllWorkLogs());
//                } catch (JiraException e) {
//                  return Observable.error(e);
//                }
//              }
//            },
//            new Func2<Issue, List<WorkLog>, Pair<Issue, List<WorkLog>>>() {
//              @Override
//              public Pair<Issue, List<WorkLog>> call(Issue issue, List<WorkLog> workLogs) {
//                for (Iterator<WorkLog> logIterator = workLogs.iterator(); logIterator.hasNext(); )
//                  if (filterer.valid(logIterator.next()) == null)
//                    logIterator.remove();
//                return new Pair<>(issue, workLogs);
//              }
//            }
//        );
  }

  /**
   * Returns a date range from observable
   * @param start start date
   * @param end end date
   * @return
   */
  public static Observable<String> issueSearchDateRangeObservable(DateTime start, DateTime end, String user) {
//    return Observable.create(new Observable.OnSubscribe<String>() {
//      @Override
//      public void call(Subscriber<? super String> subscriber) {
//        if (start == null) {
//          subscriber.onError(new IllegalArgumentException("Start date invalid"));
//          return;
//        }
//        if (end == null) {
//          subscriber.onError(new IllegalArgumentException("End date invalid"));
//          return;
//        }
//        if (user == null) {
//          subscriber.onError(new IllegalArgumentException("Invalid user"));
//          return;
//        }
//        DateTime startSearchDate = start.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
//        DateTime endSearchDate = end.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
//        subscriber.onNext(
//                String.format(
//                        Const.INSTANCE.getDEFAULT_JQL_WORKLOG_TEMPLATE(),
//                        JiraSearchSubscriberImpl.Companion.getDateFormat().print(startSearchDate.getMillis()),
//                        JiraSearchSubscriberImpl.Companion.getDateFormat().print(endSearchDate.getMillis()),
//                        user
//                )
//        );
//        subscriber.onCompleted();
//      }
//    });
    return Observable.empty();
  }

  /**
   * Observable for pulling all the user issues
   * @param jql
   * @return
   */
  @Deprecated
  public static Observable<Issue> userIssues(JiraClient client, String jql) {
//    return Observable.create(new JiraSearchSubscriberImpl(client, jql, "summary,project,created,updated"))
//    return Observable.create(new JiraSearchSubscriberImpl(client, jql, "summary,project,created,updated"))
//        .flatMap(searchResult -> {
//          if (searchResult.issues.size() == 0)
//            return Observable.empty();
//          return Observable.from(searchResult.issues);
//        });
    return Observable.empty();
  }

}
