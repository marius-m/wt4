package lt.markmerkk.ui

/**
 * Helps opening dialog by using external source
 */
interface ExternalSourceNode<out Node> {
    fun rootNode(): Node
}