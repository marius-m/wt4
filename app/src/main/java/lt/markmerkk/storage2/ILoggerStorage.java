package lt.markmerkk.storage2;

import java.util.Observable;
import javafx.collections.ObservableList;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/13/15.
 * Represents the exposed methods for handling simple events on the database.
 */
public interface ILoggerStorage<T> {

  /**
   * Sets target date
   * @param targetDate
   */
  void setTargetDate(String targetDate);

  /**
   * Registers to the event reporter
   * @param listener
   */
  void register(ILoggerListener<T> listener);

  /**
   * Unregisters from the reporter
   * @param listener
   */
  void unregister(ILoggerListener<T> listener);

  /**
   * Inserts a data entity
   * @param dataEntity provided data entity
   */
  void insert(T dataEntity);

  /**
   * Deletes a data entity
   * @param dataEntity provided data entity
   */
  void delete(T dataEntity);

  /**
   * Updates a data entity
   * @param dataEntity provided data entity
   */
  void update(T dataEntity);

  /**
   * Notifies logs have changed and needs a refresh
   */
  void notifyDataChange();

  /**
   * Get currently loaded logs
   */
  ObservableList<T> getData();

}
