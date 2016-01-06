package lt.markmerkk.ui.clock.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import lt.markmerkk.storage2.SimpleLog;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/11/15.
 * A simple {@link DatePicker} date converter class
 */
public class SimpleDatePickerConverter extends StringConverter<LocalDate> {
  String pattern = "yyyy-MM-dd";
  DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

  @Override public String toString(LocalDate date) {
    if (date != null)
      return dateFormatter.format(date);
    return "";
  }

  @Override public LocalDate fromString(String string) {
    if (string != null && !string.isEmpty())
      return LocalDate.parse(string, dateFormatter);
    return null;
  }
}
