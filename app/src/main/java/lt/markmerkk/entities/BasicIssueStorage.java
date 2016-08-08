package lt.markmerkk.entities;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.entities.jobs.DeleteJob;
import lt.markmerkk.entities.jobs.InsertJob;
import lt.markmerkk.entities.jobs.QueryListJob;
import lt.markmerkk.entities.jobs.UpdateJob;
import lt.markmerkk.mvp.IDataListener;
import lt.markmerkk.mvp.IDataStorage;
import lt.markmerkk.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mariusmerkevicius on 1/6/16.
 * Holds all downloaded issues for use
 */
public class BasicIssueStorage implements IDataStorage<LocalIssue> {
  @Inject DBProdExecutor executor;

  long totalIssues;
  ObservableList<LocalIssue> issues;
  List<IDataListener<LocalIssue>> listeners;

  String filter;

  public BasicIssueStorage() {
    issues = FXCollections.observableArrayList();
    listeners = new ArrayList<>();
  }

  @PostConstruct
  void initialize() { }

  @PreDestroy
  void destroy() {
    listeners.clear();
  }

  public void updateFilter(String filter) {
    this.filter = filter;
    notifyDataChange();
  }

  @Override
  public void register(IDataListener<LocalIssue> listener) {
    if (listener == null) return;
    listeners.add(listener);
  }

  @Override
  public void unregister(IDataListener<LocalIssue> listener) {
    if (listener == null) return;
    listeners.remove(listener);
  }

  @Override
  public void insert(LocalIssue dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new InsertJob(LocalIssue.class, dataEntity));
    notifyDataChange();
  }

  @Override
  public void delete(LocalIssue dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new DeleteJob(LocalIssue.class, dataEntity));
    notifyDataChange();
  }

  @Override
  public void update(LocalIssue dataEntity) {
    if (dataEntity == null) return;
    executor.execute(new UpdateJob(LocalIssue.class, dataEntity));
    notifyDataChange();
  }

  @Override
  public void notifyDataChange() {
    QueryListJob<LocalIssue> queryJob = new QueryListJob<>(LocalIssue.class);
    issues.clear();
    if (Utils.isEmpty(filter) && filter.length() <= 2) {
      queryJob = new QueryListJob<>(LocalIssue.class);
    } else {
      queryJob = new QueryListJob<>(LocalIssue.class, () -> "("
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
  public ObservableList<LocalIssue> getData() {
    return issues;
  }

  @NotNull
  @Override
  public List<LocalIssue> getDataAsList() {
    return new ArrayList<>(issues);
  }

  //region Convenience

  /**
   * Reports log change for all the listener
   */
  void reportDataChange() {
    for (IDataListener<LocalIssue> listener : listeners)
      listener.onDataChange(issues);
  }

  //endregion

}
