package lt.markmerkk.storage2.database.interfaces;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Responsible for creating different queries for the input class
 */
public interface IQueryHelper {
  /**
   * Main method to for a query with an input class
   * @param clazz input class
   * @return formed query
   */
  String formQuery(Class clazz) throws IllegalArgumentException;
}
