package lt.markmerkk.storage2;

import java.net.URI;
import java.net.URISyntaxException;
import lt.markmerkk.utils.Utils;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * A helper class to validate and build {@link SimpleLog} object
 */
public class SimpleLogBuilder {
  private long _id = -1;
  private long now;

  private long start;
  private long end;
  private String task;
  private String comment;

  private long duration;

  private long id;
  private String uri;
  private boolean dirty = true;
  private boolean error = false;
  private boolean deleted = false;
  private String errorMessage = null;

  /**
   * Standard creation
   * @param now
   */
  public SimpleLogBuilder(long now) {
    this.now = now;
  }

  /**
   * Standard creation
   */
  public SimpleLogBuilder() {
    this.now = -1;
  }

  /**
   * Building from remote
   * @param task
   * @param remoteLog
   */
  public SimpleLogBuilder(String task, WorkLog remoteLog) {
    if (task == null)
      throw new IllegalArgumentException("Error getting task number log!");
    if (remoteLog == null)
      throw new IllegalArgumentException("Error getting remote log!");
    this.start = remoteLog.getStarted().getTime();
    this.end = new DateTime(remoteLog.getStarted().getTime())
        .withFieldAdded(DurationFieldType.seconds(), remoteLog.getTimeSpentSeconds())
        .getMillis();
    this.task = Utils.validateTaskTitle(task);
    this.comment = remoteLog.getComment();
    this.uri = remoteLog.getSelf();
    this.id = parseUri(this.uri);

    this.deleted = false;
    this.dirty = false;
    this.error = false;
    this.errorMessage = null;
  }

  /**
   * Updating from remote
   * @param task
   * @param remoteLog
   */
  public SimpleLogBuilder(SimpleLog simpleLog, String task, WorkLog remoteLog) {
    this._id = simpleLog._id;
    if (task == null)
      throw new IllegalArgumentException("Error getting task number log!");
    if (remoteLog == null)
      throw new IllegalArgumentException("Error getting remote log!");
    this.start = remoteLog.getStarted().getTime();
    this.end = new DateTime(remoteLog.getStarted().getTime())
        .withFieldAdded(DurationFieldType.seconds(), remoteLog.getTimeSpentSeconds())
        .getMillis();
    this.task = Utils.validateTaskTitle(task);
    this.comment = remoteLog.getComment();

    this.uri = remoteLog.getSelf();
    this.id = parseUri(this.uri);
    this.deleted = false;
    this.dirty = false;
    this.error = false;
    this.errorMessage = null;
  }

  /**
   * Cloning log
   * @param log
   */
  public SimpleLogBuilder(SimpleLog log) {
    this._id = log._id;
    this.start = log.start;
    this.end = log.end;
    this.task = log.task;
    this.comment = log.comment;


    this.uri = log.uri;
    this.id = log.id;
    this.deleted = log.deleted;
    this.dirty = true;
    this.error = false;
    this.errorMessage = null;
  }

  public SimpleLogBuilder setStart(long start) {
    this.start = start;
    return this;
  }

  public SimpleLogBuilder setEnd(long end) {
    this.end = end;
    return this;
  }

  public SimpleLogBuilder setTask(String task) {
    this.task = Utils.validateTaskTitle(task);
    return this;
  }

  public SimpleLogBuilder setComment(String comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Takes in worklog uri and parses out the last part of it.
   * @return
   */
  public static long parseUri(String url) {
    if (Utils.isEmpty(url)) return 0;
    try {
      URI uri = new URI(url);
      String[] segments = uri.getPath().split("/");
      String idString = segments[segments.length-1];
      return Long.parseLong(idString);
    } catch (URISyntaxException e) {
      return 0;
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Composes a new log normally with error message
   * @param errorMessage error log
   * @return
   */
  public SimpleLog buildWithError(String errorMessage) {
    SimpleLog newSimpleLog = build();
    newSimpleLog.dirty = false;
    newSimpleLog.error = true;
    newSimpleLog.errorMessage = errorMessage;
    return newSimpleLog;
  }

  /**
   * Composes a new log with parameter validation
   * @return
   */
  public SimpleLog build() {
    if (this.start <= 0 && this.now <= 0) throw new IllegalArgumentException("Please specify start time, or current time!");
    if (this.end <= 0 && this.now <= 0) throw new IllegalArgumentException("Please specify end time, or current time!");
    if (this.start <= 0) this.start = this.now;
    if (this.end <= 0) this.end = now;
    if (this.start > this.end) throw new IllegalArgumentException("Invalid parameters!");
    //DateTime startTime = new DateTime(this.start).withSecondOfMinute(0);
    //DateTime endTime = new DateTime(this.end).withSecondOfMinute(0);
    //duration = endTime.getMillis() - startTime.getMillis();
    duration = end - start;


    SimpleLog newSimpleLog = new SimpleLog();
    newSimpleLog.start = this.start;
    newSimpleLog.end = this.end;
    newSimpleLog.duration = this.duration;
    newSimpleLog.task = normalize(this.task);
    newSimpleLog.comment = normalize(this.comment);
    newSimpleLog.uri = this.uri;
    newSimpleLog.id = this.id;

    newSimpleLog.dirty = this.dirty;
    newSimpleLog.error = this.error;
    newSimpleLog.errorMessage = normalize(this.errorMessage);
    newSimpleLog.deleted = this.deleted;

    if (this._id > 0)
      newSimpleLog._id = this._id;
    return newSimpleLog;
  }

  /**
   * Removes strings that may break functioanlity.
   * @param input
   * @return
   */
  String normalize(String input) {
    if (input == null) return null;
    return input.replaceAll("\"", "\'");
  }

}
