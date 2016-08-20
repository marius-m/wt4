package lt.markmerkk.entities.database.interfaces;

/**
 * Created by mariusmerkevicius on 11/21/15.
 * Responsible for creating different queries for the input class
 */
public interface IQuery {
  /**
   * Main method to form a query with an input class
   * @param clazz input class
   * @return formed query
   */
  String formQuery(Class clazz) throws IllegalArgumentException, UnsupportedOperationException;

  /**
   * Main method to form a query with an input class and its instance
   * @param clazz input class
   * @return formed query
   */
  String formQuery(Class clazz, DBEntity entity) throws IllegalArgumentException, UnsupportedOperationException;
}
