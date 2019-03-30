package com.jfoenix.skins;

import com.jfoenix.controls.JFXDatePicker;

/**
 * Workaround class for using {@link JFXDatePicker}'s internal popup dialog
 */
public class JFXDatePickerContentLocal extends JFXDatePickerContent {
    public JFXDatePickerContentLocal(JFXDatePicker datePicker) {
        super(datePicker);
    }
}
