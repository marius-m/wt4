package lt.markmerkk.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lt.markmerkk.utils.LogFormatters;
import lt.markmerkk.utils.LogUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Represents a worklog entity
 */
@Deprecated // Should be replaced with 'Log' eventually
//@Table(name = "Log")
public class SimpleLog extends RemoteEntity {
  //public final static DateTimeFormatter shortFormat = DateTimeFormat.forPattern("HH:mm");
  //public final static DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
  //public final static DateTimeFormatter longDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

  private static final String KEY_START = "start";
  private static final String KEY_END = "end";
  private static final String KEY_DURATION = "duration";
  private static final String KEY_TASK = "task";
  private static final String KEY_COMMENT = "comment";

//  @Column(value = FieldType.INTEGER)
  long start;
//  @Column(value = FieldType.INTEGER)
  long end;
//  @Column(value = FieldType.INTEGER)
  long duration;
//  @Column(value = FieldType.TEXT)
  String task;
//  @Column(value = FieldType.TEXT)
  String comment;
  String systemNote;
  String author;

  //region Getters / Setters

  public String getLongStart() {
    return LogFormatters.INSTANCE.getLongFormat().print(start);
  }

  public String getShortStart() {
    return LogFormatters.INSTANCE.getShortFormat().print(start);
  }

  public String getLongEnd() {
    return LogFormatters.INSTANCE.getLongFormat().print(end);
  }

  public String getShortEnd() {
    return LogFormatters.INSTANCE.getShortFormat().print(end);
  }

  public String getPrettyDuration() {
    return LogUtils.INSTANCE.formatShortDurationMillis(duration);
  }

  public long getStart() {
    return start;
  }

  public long getEnd() {
    return end;
  }

  public String getTask() {
    return task;
  }

  public String getComment() {
    return comment;
  }

  public long getDuration() {
    return duration;
  }

  public String getSystemNote() {
    return systemNote;
  }

  public String getAuthor() {
    return author;
  }

  /**
   * Defines if entity can be edited
   * @return
   */
  public boolean canEdit() {
    return !isRemote();
  }

  public boolean isRemote() {
    return id > 0;
  }

  //endregion

  //region Archiving

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    Map<String, Object> pack = super.pack();
    pack.put(KEY_START, start);
    pack.put(KEY_END, end);
    pack.put(KEY_DURATION, duration);
    pack.put(KEY_TASK, "\"" + task + "\"");
    pack.put(KEY_COMMENT, "\"" + comment + "\"");
    return pack;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    super.unpack(resultSet);
    start = resultSet.getLong(resultSet.findColumn(KEY_START));
    end = resultSet.getLong(resultSet.findColumn(KEY_END));
    duration = resultSet.getLong(resultSet.findColumn(KEY_DURATION));
    task = resultSet.getString(resultSet.findColumn(KEY_TASK));
    comment = resultSet.getString(resultSet.findColumn(KEY_COMMENT));
  }

  //endregion


  @Override
  public String toString() {
    final DateTime dateTimeStart = new DateTime(start);
    final DateTime dateTimeEnd = new DateTime(end);
    final Duration durationObj = new Duration(duration);
    return "SimpleLog{" +
            "start=" + LogFormatters.INSTANCE.getLongFormat().print(dateTimeStart) +
            ", end=" + LogFormatters.INSTANCE.getLongFormat().print(dateTimeEnd) +
            ", duration=" + durationObj.toString() +
            ", task='" + task + '\'' +
            ", comment='" + comment + '\'' +
            ", id=" + id +
            ", uri='" + uri + '\'' +
            ", deleted=" + deleted +
            ", dirty=" + dirty +
            ", error=" + error +
            ", errorMessage='" + errorMessage + '\'' +
            ", download_millis=" + download_millis +
            ", _id=" + _id +
            '}';
  }
}
