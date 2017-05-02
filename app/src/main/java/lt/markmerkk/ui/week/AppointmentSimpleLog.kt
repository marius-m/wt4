package lt.markmerkk.ui.week

import jfxtras.scene.control.agenda.Agenda
import jfxtras.scene.control.agenda.Agenda.AppointmentImplLocal
import lt.markmerkk.entities.SimpleLog

/**
 * Created by mariusmerkevicius on 1/22/16.
 * Represents an appointment for the [Agenda] that would represent a simple
 * log
 */
class AppointmentSimpleLog(val simpleLog: SimpleLog) : AppointmentImplLocal()
