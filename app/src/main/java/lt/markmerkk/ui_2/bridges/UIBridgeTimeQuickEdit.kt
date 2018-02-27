package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXTimePicker
import javafx.scene.layout.StackPane
import lt.markmerkk.mvp.TimeQuickModifier
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIBridge
import java.time.LocalDateTime

/**
 * Represents controls to quickly edit time
 */
class UIBridgeTimeQuickEdit(
        private val jfxSubtractFrom: JFXButton,
        private val jfxSubtractTo: JFXButton,
        private val jfxAppendFrom: JFXButton,
        private val jfxAppendTo: JFXButton,
        private val jfxDateFrom: JFXDatePicker,
        private val jfxTimeFrom: JFXTimePicker,
        private val jfxDateTo: JFXDatePicker,
        private val jfxTimeTo: JFXTimePicker,
        private val timeQuickModifier: TimeQuickModifier
) : UIBridge {

    init {
        jfxSubtractFrom.setOnAction {
            timeQuickModifier.subtractStartTime(
                    LocalDateTime.of(
                            jfxDateFrom.value,
                            jfxTimeFrom.value
                    ),
                    LocalDateTime.of(
                            jfxDateTo.value,
                            jfxTimeTo.value
                    )
            )
        }
        jfxAppendFrom.setOnAction {
            timeQuickModifier.appendStartTime(
                    LocalDateTime.of(
                            jfxDateFrom.value,
                            jfxTimeFrom.value
                    ),
                    LocalDateTime.of(
                            jfxDateTo.value,
                            jfxTimeTo.value
                    )
            )
        }
        jfxSubtractTo.setOnAction {
            timeQuickModifier.subtractEndTime(
                    LocalDateTime.of(
                            jfxDateFrom.value,
                            jfxTimeFrom.value
                    ),
                    LocalDateTime.of(
                            jfxDateTo.value,
                            jfxTimeTo.value
                    )
            )
        }
        jfxAppendTo.setOnAction {
            timeQuickModifier.appendEndTime(
                    LocalDateTime.of(
                            jfxDateFrom.value,
                            jfxTimeFrom.value
                    ),
                    LocalDateTime.of(
                            jfxDateTo.value,
                            jfxTimeTo.value
                    )
            )
        }
    }

    fun enable() {
        jfxSubtractFrom.isDisable = false
        jfxAppendFrom.isDisable = false
        jfxSubtractTo.isDisable = false
        jfxAppendTo.isDisable = false
    }

    fun disable() {
        jfxSubtractFrom.isDisable = true
        jfxAppendFrom.isDisable = true
        jfxSubtractTo.isDisable = true
        jfxAppendTo.isDisable = true
    }

}