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

/**
 * Created by mariusmerkevicius on 1/6/16.
 * Holds all downloaded issues for use
 */
public class BasicIssueStorage implements IDataStorage<SimpleIssue> {
  @Inject DBProdExecutor executor;

  ObservableList<SimpleIssue> issues;
  List<IDataListener<SimpleIssue>> listeners;

  String filter;

  public BasicIssueStorage() {
    issues = FXCollections.observableArrayList();
    listeners = new ArrayList<>();
  }

  @PostConstruct
  void initualize() { }

  @PreDestroy
  void destroy() {
    listeners.clear();
  }

  public void updateFilter(String filter) {
    this.filter = filter;
    notifyDataChange();
  }

  @Override
  public void register(IDataListener<SimpleIssue> listener) {
    if (listener == null) return;
    listeners.add(listener);
  }

  @Override
  public void unregister(IDataListener<SimpleIssue> listener) {
    if (listener == null) return;
    listeners.remove(listener);
  }

  @Override
  public void insert(SimpleIssue dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new InsertJob(SimpleIssue.class, dataEntity));
    notifyDataChange();
  }

  @Override
  public void delete(SimpleIssue dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new DeleteJob(SimpleIssue.class, dataEntity));
    notifyDataChange();
  }

  @Override
  public void update(SimpleIssue dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new UpdateJob(SimpleIssue.class, dataEntity));
    notifyDataChange();
  }

  @Override
  public void notifyDataChange() {
    QueryListJob<SimpleIssue> queryJob = new QueryListJob<>(SimpleIssue.class);
    issues.clear();
    if (Utils.isEmpty(filter) && filter.length() <= 2) {
      queryJob = new QueryListJob<>(SimpleIssue.class);
    } else {
      queryJob = new QueryListJob<>(SimpleIssue.class, () -> "("
          + "key like '%" + filter + "%' "
          + "OR "
          + "description like '%" + filter + "%' "
          + ")");
    }
    executor.execute(queryJob);
    if (queryJob.result() != null)
      issues.addAll(queryJob.result());
    reportDataChange();
  }

  @Override
  public ObservableList<SimpleIssue> getData() {
    return issues;
  }

  //region Convenience

  /**
   * Reports log change for all the listener
   */
  void reportDataChange() {
    for (IDataListener<SimpleIssue> listener : listeners)
      listener.onDataChange(issues);
  }

  //endregion

}
