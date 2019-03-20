package lt.markmerkk.ui_2

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.stage.Stage

class StageProperties(
        val stage: Stage,
        val propertyWidth: ReadOnlyDoubleProperty = stage.widthProperty(),
        val propertyHeight: ReadOnlyDoubleProperty = stage.heightProperty(),
        val propertyFocus: ReadOnlyBooleanProperty = stage.focusedProperty()
)