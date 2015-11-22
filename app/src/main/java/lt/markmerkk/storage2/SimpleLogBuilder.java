package lt.markmerkk.storage2;

import lt.markmerkk.storage2.entities.SimpleLog;
import lt.markmerkk.utils.Utils;

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

  public SimpleLogBuilder(long now) {
    this.now = now;
  }

  public SimpleLogBuilder(SimpleLog log) {
    this._id = log.get_id();
    this.start = log.getStart();
    this.end = log.getEnd();
    this.task = log.getTask();
    this.comment = log.getComment();
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

  public SimpleLog build() {
    if (this.start == 0) this.start = this.now;
    if (this.end == 0) this.end = now;
    if (this.start > this.end) throw new IllegalArgumentException("Invalid parameters!");
    duration = end - start;
    SimpleLog newSimpleLog =
        new SimpleLog(this.start, this.end, this.duration, this.task, this.comment);
    if (_id > 0)
      newSimpleLog.updateIndex(_id);
    return newSimpleLog;
  }

}
