package com.jfoenix.skins;

import com.jfoenix.controls.JFXDatePicker;

/**
 * Workaround class for using {@link JFXDatePicker}'s internal popup dialog
 */
public class JFXDatePickerContentLocal extends JFXDatePickerContent {

    private final JFXDatePicker jfxDatePicker;

    public JFXDatePickerContentLocal(JFXDatePicker datePicker) {
        super(datePicker);
        this.jfxDatePicker = datePicker;
    }

    public JFXDatePicker getJfxDatePicker() {
        return jfxDatePicker;
    }
}
