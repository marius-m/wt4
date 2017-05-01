package lt.markmerkk.ui_2.bridges

import javafx.scene.layout.StackPane

/**
 * Helps opening dialog by using external source
 */
interface ExternalSourceNode {
    fun rootNode(): StackPane
}