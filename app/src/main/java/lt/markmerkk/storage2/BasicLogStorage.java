package lt.markmerkk.storage2;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryListJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.HourGlass;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/13/15.
 * Represents the storage for simple use.
 */
public class BasicLogStorage implements ILoggerStorage<SimpleLog> {
  @Inject DBProdExecutor executor;

  ObservableList<SimpleLog> logs;
  List<ILoggerListener<SimpleLog>> listeners;
  DateTime targetDate;

  public BasicLogStorage() {
    listeners = new ArrayList<>();
  }

  @PostConstruct
  void initualize() {
    setTargetDate(HourGlass.longFormat.print(DateTime.now()));
  }

  @PreDestroy
  void destroy() {
    listeners.clear();
  }

  @Override public void setTargetDate(String targetDate) {
    if (targetDate == null) return;
    try {
      DateTime dateTime = HourGlass.longFormat.parseDateTime(targetDate);
      this.targetDate = dateTime
          .withHourOfDay(0)
          .withMinuteOfHour(0)
          .withSecondOfMinute(0);
      notifyDataChange();
    } catch (IllegalArgumentException e) { }
  }

  public DateTime getTargetDate() {
    return targetDate;
  }

  @Override public void register(ILoggerListener listener) {
    if (listener == null) return;
    listeners.add(listener);
  }

  @Override public void unregister(ILoggerListener listener) {
    if (listener == null) return;
    listeners.remove(listener);
  }

  /**
   * Counts total time spent for the day
   */
  private long countTotal() {
    long total = 0;
    for (SimpleLog log : logs)
      total += log.getDuration();
    return total;
  }

  @Override public void insert(SimpleLog dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new InsertJob(SimpleLog.class, dataEntity));
    notifyDataChange();
  }

  @Override public void delete(SimpleLog dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new DeleteJob(SimpleLog.class, dataEntity));
    notifyDataChange();
  }

  @Override public void update(SimpleLog dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new UpdateJob(SimpleLog.class, dataEntity));
    notifyDataChange();
  }

  @Override public void notifyDataChange() {
    QueryListJob<SimpleLog> queryJob = new QueryListJob<>(SimpleLog.class,
        () -> "(start > " + targetDate.getMillis()
            + " AND "
            + "end < " + targetDate.plusDays(1).getMillis() + ") ORDER BY start ASC");
    executor.execute(queryJob);
    if (logs == null)
      logs = FXCollections.observableArrayList();
    logs.clear();
    if (queryJob.result() != null)
      logs.addAll(queryJob.result());
    reportDataChange();
    countTotal();
  }

  @Override public ObservableList<SimpleLog> getData() {
    return logs;
  }

  public String getTotal() {
    return Utils.formatShortDuration(countTotal());
  }

  //region Convenience

  /**
   * Reports log change for all the listener
   */
  void reportDataChange() {
    for (ILoggerListener<SimpleLog> listener : listeners)
      listener.onDataChange(logs);
  }

  //endregion



}
