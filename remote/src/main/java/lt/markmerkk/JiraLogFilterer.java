package lt.markmerkk;

import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 1/30/16.
 */
public class JiraLogFilterer {
  Logger logger = LoggerFactory.getLogger(JiraLogFilterer.class);

  String user;
  DateTime start, end;
  WorkLog workLog;

  public JiraLogFilterer(String user, DateTime start, DateTime end) {
    this.user = user;
    this.start = start;
    this.end = end;
  }

  /**
   * Filters {@link WorkLog} and returns it. If worklog fails validation, null is returned.
   *
   * @param workLog worklog to check
   */
  public WorkLog filterLog(WorkLog workLog) {
    try {
      if (user == null) throw new FilterErrorException("user == null");
      if (start == null) throw new FilterErrorException("start == null");
      if (end == null) throw new FilterErrorException("end == null");
      if (workLog == null) throw new FilterErrorException("worklog == null");
      if (!workLog.getAuthor().getName().equals(user))  throw new FilterErrorException("invalid user");
      if (start.isAfter(workLog.getStarted().getTime()))  throw new FilterErrorException("start is after worklog date");
      if (end.isBefore(workLog.getStarted().getTime()))  throw new FilterErrorException("end is before worklog date");
      return workLog;
    } catch (FilterErrorException e) {
      logger.debug("Ignoring "+workLog+" for because "+e.getMessage());
      return null;
    }
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
