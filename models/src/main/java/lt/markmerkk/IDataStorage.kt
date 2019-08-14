package lt.markmerkk

/**
 * Represents the exposed methods for handling simple events on the database.
 */
interface IDataStorage<T> {

    /**
     * Registers to the event reporter
     * @param listener
     */
    fun register(listener: IDataListener<T>)

    /**
     * Unregisters from the reporter
     * @param listener
     */
    fun unregister(listener: IDataListener<T>)

    /**
     * Inserts a data entity
     * @param dataEntity provided data entity
     */
    fun insert(dataEntity: T)

    /**
     * Deletes a data entity
     * @param dataEntity provided data entity
     */
    fun delete(dataEntity: T)

    /**
     * Updates a data entity
     * @param dataEntity provided data entity
     */
    fun update(dataEntity: T)

    /**
     * Notifies logs have changed and needs a refresh
     */
    fun notifyDataChange()

    /**
     * Finds item by id or null
     */
    fun findByIdOrNull(id: Long): T?

    /**
     * Get currently loaded logs
     */
    val data: List<T>

}
