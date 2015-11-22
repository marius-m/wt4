package lt.markmerkk.storage2.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lt.markmerkk.storage2.database.annotations.Column;
import lt.markmerkk.storage2.database.annotations.FieldType;
import lt.markmerkk.storage2.database.annotations.Table;

/**
 * Created by mariusmerkevicius on 11/20/15.
 * Represents a worklog entity
 */
@Table(name = "Log")
public class SimpleLog extends RemoteEntity {
  private static final String KEY_START = "start";
  private static final String KEY_END = "end";
  private static final String KEY_TASK = "task";
  private static final String KEY_COMMENT = "comment";

  @Column(value = FieldType.INTEGER)
  long start;
  @Column(value = FieldType.INTEGER)
  long end;
  @Column(value = FieldType.TEXT)
  String task;
  @Column(value = FieldType.TEXT)
  String comment;

  public SimpleLog() { }

  public SimpleLog(long start, long end, String task, String comment) {
    this.start = start;
    this.end = end;
    this.task = task;
    this.comment = comment;
  }

  //region Getters / Setters

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public long getEnd() {
    return end;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public String getTask() {
    return task;
  }

  public void setTask(String task) {
    this.task = task;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
  //endregion

  //region Archiving

  @Override public Map<String, Object> pack() throws IllegalArgumentException {
    Map<String, Object> pack = super.pack();
    pack.put(KEY_START, start);
    pack.put(KEY_END, end);
    pack.put(KEY_TASK, "\"" + task + "\"");
    pack.put(KEY_COMMENT, "\"" + comment + "\"");
    return pack;
  }

  @Override public void unpack(ResultSet resultSet) throws IllegalArgumentException, SQLException {
    super.unpack(resultSet);
    start = resultSet.getLong(resultSet.findColumn(KEY_START));
    end = resultSet.getLong(resultSet.findColumn(KEY_END));
    task = resultSet.getString(resultSet.findColumn(KEY_TASK));
    comment = resultSet.getString(resultSet.findColumn(KEY_COMMENT));
  }

  //endregion

}
