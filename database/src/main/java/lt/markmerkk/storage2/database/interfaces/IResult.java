package lt.markmerkk.storage2.database.interfaces;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * Will hold the result for the query.
 */
public interface IResult<T> {
  /**
   * Method to hold the result after execution
   * @return retsult
   */
  T result();
}
