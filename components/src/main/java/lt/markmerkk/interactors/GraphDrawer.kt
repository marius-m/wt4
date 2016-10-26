package lt.markmerkk.interactors

import javafx.scene.layout.Region

/**
 * @author mariusmerkevicius
 * @since 2016-10-26
 */
interface GraphDrawer {
    val title: String
    fun createGraph(): Region
}