package lt.markmerkk.ui.week;

import jfxtras.scene.control.agenda.Agenda;
import lt.markmerkk.storage2.SimpleLog;

/**
 * Created by mariusmerkevicius on 1/22/16.
 * Represents an appointment for the {@link Agenda} that would represent a simple
 * log
 */
public class AppointmentSimpleLog extends Agenda.AppointmentImplLocal {
  SimpleLog simpleLog;
  public AppointmentSimpleLog(SimpleLog log) {
    if (log == null)
      throw new IllegalArgumentException("log == null");
    this.simpleLog = log;
  }

  public SimpleLog getSimpleLog() {
    return simpleLog;
  }
}
