package lt.markmerkk.ui.week;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

  List<LocalDate> dates;
  LocalDate startDate;
  LocalDate endDate;

  public CustomAgendaWeekView(Agenda control) {
    super(control);
  }

  /**
   * Assign a calendar to each day, so it knows what it must draw.
   */
  protected List<LocalDate> determineDisplayedLocalDates() {
    DateTime target = WeekPresenter.targetDate
        .withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
    if (isTargetBetween(target)) return dates; // Ignoring unnecessary recalculations
    if (dates == null)
      dates = new ArrayList<>();
    dates.clear();
    startDate = LocalDate.of(target.getYear(), target.getMonthOfYear(), target.getDayOfMonth());
    endDate = startDate.plusDays(6);
    for (int i = 0; i < 7; i++)
      dates.add(startDate.plusDays(i));
    return dates;
  }

  //region Convenience

  /**
   * Returns if target between the {@link #startDate} and {@link #endDate}
   * @param target
   * @return
   */
  public boolean isTargetBetween(DateTime target) {
    if (target == null)
      throw new IllegalArgumentException("target == null");
    if (startDate == null) return false;
    if (endDate == null) return false;
    LocalDate targetDate = LocalDate.of(
        target.getYear(), target.getMonthOfYear(), target.getDayOfMonth());
    return ( (targetDate.isBefore(endDate) || targetDate.isEqual(endDate)) &&
        (targetDate.isAfter(startDate) || targetDate.isEqual(startDate)));
  }

  //endregion

}
