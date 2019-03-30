package lt.markmerkk.ui_2

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.stage.Stage
import lt.markmerkk.Main

class StageProperties(
        stage: Stage,
        private val propertyWidth: ReadOnlyDoubleProperty = stage.widthProperty(),
        private val propertyHeight: ReadOnlyDoubleProperty = stage.heightProperty(),
        private val propertyFocus: ReadOnlyBooleanProperty = stage.focusedProperty()
) {

    var width: Double = Main.SCENE_WIDTH.toDouble()
        private set
    var height: Double = Main.SCENE_HEIGHT.toDouble()
        private set
    var focus: Boolean = true
        private set

    private var listeners: List<StageChangeListener> = emptyList()
    private val changeListenerWidth: (ObservableValue<out Number>, Number, Number) -> Unit = { _, _, newValue ->
        this.width = newValue.toDouble()
        this.listeners.forEach { it.onNewWidth(this.width) }
    }
    private val changeListenerHeight: (ObservableValue<out Number>, Number, Number) -> Unit = { _, _, newValue ->
        this.height = newValue.toDouble()
        this.listeners.forEach { it.onNewHeight(this.height) }
    }
    private val changeListenerFocus: (ObservableValue<out Boolean>, Boolean, Boolean) -> Unit = { _, _, newValue ->
        this.focus = newValue
        this.listeners.forEach { it.onFocusChange(this.focus) }
    }

    fun onAttach() {
        propertyWidth.addListener(changeListenerWidth)
        propertyHeight.addListener(changeListenerHeight)
        propertyFocus.addListener(changeListenerFocus)
    }

    fun onDetach() {
        propertyWidth.removeListener(changeListenerWidth)
        propertyHeight.removeListener(changeListenerHeight)
        propertyFocus.removeListener(changeListenerFocus)
    }

    fun register(listener: StageChangeListener) {
        this.listeners = listeners.plus(listener)
    }

    fun unregister(listener: StageChangeListener) {
        this.listeners = listeners.minus(listener)
    }

    interface StageChangeListener {
        fun onNewWidth(newWidth: Double)
        fun onNewHeight(newHeight: Double)
        fun onFocusChange(focus: Boolean)
    }
}