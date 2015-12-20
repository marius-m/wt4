package lt.markmerkk.listeners;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents an interface, that indicates class can be updated with input data.
 * Mainly used with MVP model for more control over presenter class.
 */
public interface Editable<UpdateObject> {
  /**
   * Method invoked when some object must be updates
   * @param object input object
   */
  void onObjectEdit(UpdateObject object);
}
