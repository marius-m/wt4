package lt.markmerkk.widgets

import com.jfoenix.controls.JFXTextField
import lt.markmerkk.TimeRangeGenerator

class TimeRangeSourceTextfield(
    private val textfield: JFXTextField
): TimeRangeGenerator.Source {
    override fun rawInput(): String {
        return textfield.text
    }
}

fun JFXTextField.wrapAsSource(): TimeRangeSourceTextfield {
    return TimeRangeSourceTextfield(this)
}