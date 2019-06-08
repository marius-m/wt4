package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXTimePicker
import lt.markmerkk.TimeProvider
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.validators.QuickTimeModifyValidator
import lt.markmerkk.validators.TimeGap
import lt.markmerkk.ui.UIBridge

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
        private val logEditService: LogEditService,
        private val timeProvider: TimeProvider
) : UIBridge {

    private val timeEditValidator = QuickTimeModifyValidator

    init {
        jfxSubtractFrom.setOnAction {
            val dateTimeStart = timeProvider.toJodaDateTime(
                    jfxDateFrom.value,
                    jfxTimeFrom.value
            )
            val dateTimeEnd = timeProvider.toJodaDateTime(
                    jfxDateTo.value,
                    jfxTimeTo.value
            )
            val newTimeGap = timeEditValidator.expandToStart(
                    TimeGap.from(dateTimeStart, dateTimeEnd),
                    minutes = 1
            )
            logEditService.updateDateTime(newTimeGap.start, newTimeGap.end)
            logEditService.redraw()
        }
        jfxAppendFrom.setOnAction {
            val dateTimeStart = timeProvider.toJodaDateTime(
                    jfxDateFrom.value,
                    jfxTimeFrom.value
            )
            val dateTimeEnd = timeProvider.toJodaDateTime(
                    jfxDateTo.value,
                    jfxTimeTo.value
            )
            val newTimeGap = timeEditValidator.shrinkFromStart(
                    TimeGap.from(dateTimeStart, dateTimeEnd),
                    minutes = 1
            )
            logEditService.updateDateTime(newTimeGap.start, newTimeGap.end)
            logEditService.redraw()
        }
        jfxSubtractTo.setOnAction {
            val dateTimeStart = timeProvider.toJodaDateTime(
                    jfxDateFrom.value,
                    jfxTimeFrom.value
            )
            val dateTimeEnd = timeProvider.toJodaDateTime(
                    jfxDateTo.value,
                    jfxTimeTo.value
            )
            val newTimeGap = timeEditValidator.shrinkFromEnd(
                    TimeGap.from(dateTimeStart, dateTimeEnd),
                    minutes = 1
            )
            logEditService.updateDateTime(newTimeGap.start, newTimeGap.end)
            logEditService.redraw()
        }
        jfxAppendTo.setOnAction {
            val dateTimeStart = timeProvider.toJodaDateTime(
                    jfxDateFrom.value,
                    jfxTimeFrom.value
            )
            val dateTimeEnd = timeProvider.toJodaDateTime(
                    jfxDateTo.value,
                    jfxTimeTo.value
            )
            val newTimeGap = timeEditValidator.expandToEnd(
                    TimeGap.from(dateTimeStart, dateTimeEnd),
                    minutes = 1
            )
            logEditService.updateDateTime(newTimeGap.start, newTimeGap.end)
            logEditService.redraw()
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