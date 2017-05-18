package lt.markmerkk.ui

import javafx.scene.layout.StackPane

/**
 * Helps opening dialog by using external source
 */
interface ExternalSourceNode<out Node> {
    fun rootNode(): Node
}