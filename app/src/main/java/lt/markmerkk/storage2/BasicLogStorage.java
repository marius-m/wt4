package lt.markmerkk.storage2;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryListJob;
import lt.markmerkk.storage2.jobs.UpdateJob;
import lt.markmerkk.ui.utils.DisplayType;
import lt.markmerkk.utils.LogFormatters;
import lt.markmerkk.utils.LogUtils;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.HourGlass;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 * Created by mariusmerkevicius on 12/13/15.
 * Represents the storage for simple use.
 */
public class BasicLogStorage implements IDataStorage<SimpleLog> {
  IExecutor executor;

  ObservableList<SimpleLog> logs;
  List<IDataListener<SimpleLog>> listeners;
  DateTime targetDate;
  DisplayType displayType = DisplayType.DAY;

  public BasicLogStorage(IExecutor executor) {
    if (executor == null) throw new IllegalArgumentException("Executor == null");
    this.executor = executor;
    listeners = new ArrayList<>();
    setTargetDate(LogFormatters.INSTANCE.getLongFormat().print(DateTime.now()));
  }

  @PreDestroy
  void destroy() {
    listeners.clear();
  }

  public void setTargetDate(String targetDate) {
    if (targetDate == null) return;
    try {
      DateTime newTime = LogFormatters.INSTANCE.getLongFormat().parseDateTime(targetDate);
      if (this.targetDate != null
          && this.targetDate.getYear() == newTime.getYear()
          && this.targetDate.getMonthOfYear() == newTime.getMonthOfYear()
          && this.targetDate.getDayOfMonth() == newTime.getDayOfMonth())
        return;
      this.targetDate = newTime
          .withHourOfDay(0)
          .withMinuteOfHour(0)
          .withSecondOfMinute(0);
      notifyDataChange();
    } catch (IllegalArgumentException e) { }
  }

  @Override public void register(IDataListener listener) {
    if (listener == null) return;
    listeners.add(listener);
  }

  @Override public void unregister(IDataListener listener) {
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
    long start = System.currentTimeMillis();
    QueryListJob<SimpleLog> queryJob;
    switch (displayType) {
      case WEEK:
        DateTime weekStart = targetDate
            .withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        DateTime weekEnd = targetDate.withDayOfWeek(DateTimeConstants.SUNDAY)
            .plusDays(1).withTimeAtStartOfDay();
        queryJob = new QueryListJob<>(SimpleLog.class,
            () -> "(start > " + weekStart.getMillis()
                + " AND "
                + "start < " + weekEnd.getMillis() + ") ORDER BY start ASC");
        break;
      default:
        queryJob = new QueryListJob<>(SimpleLog.class,
            () -> "(start > " + targetDate.getMillis()
                + " AND "
                + "start < " + targetDate.plusDays(1).getMillis() + ") ORDER BY start ASC");
    }
    executor.execute(queryJob);
//    System.out.println("Query in " + (System.currentTimeMillis() - start) + "ms");
    if (logs == null)
      logs = FXCollections.observableArrayList();
    logs.clear();
    if (queryJob.result() != null)
      logs.addAll(queryJob.result());
    reportDataChange();
    countTotal();
  }

  //region Getters / Setters

  @Override public ObservableList<SimpleLog> getData() {
    return logs;
  }

  public DateTime getTargetDate() {
    return targetDate;
  }

  public DisplayType getDisplayType() {
    return displayType;
  }

  public void setDisplayType(DisplayType displayType) {
    if (this.displayType == displayType) return;
    this.displayType = displayType;
    notifyDataChange();
  }

  //endregion

  public String getTotal() {
    return LogUtils.INSTANCE.formatShortDuration(countTotal());
  }

  //region Convenience

  /**
   * Reports log change for all the listener
   */
  void reportDataChange() {
    for (IDataListener<SimpleLog> listener : listeners) {
      long start = System.currentTimeMillis();
      listener.onDataChange(logs);
//      System.out.println("Update for "+listener+" in " + (System.currentTimeMillis() - start) + "ms");
    }
  }

  //endregion



}
