package lt.markmerkk.ui.week;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jfxtras.internal.scene.control.skin.agenda.AgendaWeekSkin;
import jfxtras.internal.scene.control.skin.agenda.base24hour.AgendaSkinTimeScale24HourAbstract;
import jfxtras.scene.control.agenda.Agenda;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 * Created by mariusmerkevicius on 2/7/16.
 * custom week view for the agenda
 */
public class CustomAgendaWeekView extends AgendaSkinTimeScale24HourAbstract<AgendaWeekSkin> {

  public CustomAgendaWeekView(Agenda control) {
    super(control);
  }

  /**
   * Assign a calendar to each day, so it knows what it must draw.
   */
  protected List<LocalDate> determineDisplayedLocalDates() {
    DateTime start = WeekPresenter.targetDate
        .withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
    List<LocalDate> lLocalDates = new ArrayList<>();
    LocalDate lStartLocalDate = LocalDate.of(
        start.getYear(), start.getMonthOfYear(), start.getDayOfMonth());
    for (int i = 0; i < 7; i++)
      lLocalDates.add(lStartLocalDate.plusDays(i));
    return lLocalDates;
  }

}
