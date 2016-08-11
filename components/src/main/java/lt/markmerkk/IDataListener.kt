package lt.markmerkk

import javafx.collections.ObservableList

/**
 * A callback listener when some event occurs on
 * storage
 */
interface IDataListener<T> {
    /**
     * Called when new data is available
     * @param data
     */
    fun onDataChange(data: ObservableList<T>)

}
