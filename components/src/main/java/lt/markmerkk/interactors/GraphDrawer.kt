package lt.markmerkk.interactors

import javafx.scene.layout.Region

/**
 * @author mariusmerkevicius
 * @since 2016-10-26
 */
interface GraphDrawer<T> {
    val title: String
    fun populateData(data: List<T>)
    fun createGraph(): Region
}