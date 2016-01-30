package lt.markmerkk;

import net.rcarz.jiraclient.WorkLog;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Filter;
import org.joda.time.DateTime;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by mariusmerkevicius on 1/30/16.
 */
public class JiraLogFilterer implements Observable.OnSubscribe<WorkLog> {
  Logger logger = Logger.getLogger(JiraLogFilterer.class);

  String user;
  DateTime start, end;
  WorkLog workLog;

  public JiraLogFilterer(String user, DateTime start, DateTime end, WorkLog workLog) {
    this.user = user;
    this.start = start;
    this.end = end;
    this.workLog = workLog;
  }

  @Override
  public void call(Subscriber<? super WorkLog> subscriber) {
    try {
      subscriber.onNext(filterLog(user, start, end, workLog));
      subscriber.onCompleted();
    } catch (FilterErrorException e) {
      logger.debug(e.getMessage());
    }
  }

  /**
   * Filters {@link WorkLog} and returns it. If worklog fails validation, null is returned.
   *
   * @param user provided user for the worklog
   * @param startSearchDate provided start search date
   * @param endSearchDate provided end search date
   * @param workLog worklog to check
   */
  WorkLog filterLog(String user, DateTime startSearchDate, DateTime endSearchDate, WorkLog workLog)
      throws FilterErrorException {
    if (user == null) throw new FilterErrorException("user == null");
    if (startSearchDate == null) throw new FilterErrorException("start == null");
    if (endSearchDate == null) throw new FilterErrorException("end == null");
    if (workLog == null) throw new FilterErrorException("worklog == null");
    if (!workLog.getAuthor().getName().equals(user))  throw new FilterErrorException("invalid user");
    if (startSearchDate.isAfter(workLog.getStarted().getTime()))  throw new FilterErrorException("start is after worklog date");
    if (endSearchDate.isBefore(workLog.getStarted().getTime()))  throw new FilterErrorException("end is before worklog date");
    return workLog;
  }

  //region Classes

  /**
   * Thrown whenever there is a problem filtering some {@link WorkLog}
   */
  public class FilterErrorException extends Exception {
    public FilterErrorException(String message) {
      super(message);
    }
  }

  //endregion

}
