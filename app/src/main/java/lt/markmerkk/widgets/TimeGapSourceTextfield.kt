package lt.markmerkk.widgets

import com.jfoenix.controls.JFXTextField
import lt.markmerkk.TimeGapGenerator

class TimeGapSourceTextfield(
    private val textfield: JFXTextField
): TimeGapGenerator.Source {
    override fun rawInput(): String {
        return textfield.text
    }
}

fun JFXTextField.wrapAsSource(): TimeGapSourceTextfield {
    return TimeGapSourceTextfield(this)
}