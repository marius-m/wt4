package lt.markmerkk

import javafx.scene.Node


fun Node.showIf(predicate: Boolean) {
    isVisible = predicate
    isManaged = predicate
}

