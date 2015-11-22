package lt.markmerkk.storage2.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import lt.markmerkk.storage2.database.annotations.Column;
import lt.markmerkk.storage2.database.annotations.FieldType;
import lt.markmerkk.storage2.database.annotations.Table;
import lt.markmerkk.utils.Utils;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Represents a worklog entity
 */
@Table(name = "Log")
public class SimpleLog extends RemoteEntity {
  public final static DateTimeFormatter shortFormat = DateTimeFormat.forPattern("HH:mm");
  public final static DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  private static final String KEY_START = "start";
  private static final String KEY_END = "end";
  private static final String KEY_DURATION = "duration";
  private static final String KEY_TASK = "task";
  private static final String KEY_COMMENT = "comment";

  @Column(value = FieldType.INTEGER)
  long start;
  @Column(value = FieldType.INTEGER)
  long end;
  @Column(value = FieldType.INTEGER)
  long duration;
  @Column(value = FieldType.TEXT)
  String task;
  @Column(value = FieldType.TEXT)
  String comment;

  public SimpleLog() { }

  public SimpleLog(long start, long end, long duration, String task, String comment) {
    this.start = start;
    this.end = end;
    this.task = task;
    this.duration = duration;
    this.comment = comment;
  }

  //region Getters / Setters

  public String getLongStart() {
    return longFormat.print(start);
  }

  public String getShortStart() {
    return shortFormat.print(start);
  }

  public String getLongEnd() {
    return longFormat.print(end);
  }

  public String getShortEnd() {
    return shortFormat.print(end);
  }

  public String getPrettyDuration() {
    return Utils.formatDuration(duration);
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

}
