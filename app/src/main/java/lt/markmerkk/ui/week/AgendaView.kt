package lt.markmerkk.ui.week

import jfxtras.scene.control.agenda.Agenda

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface AgendaView {
    fun updateAgenda(appointments: List<Agenda.AppointmentImplLocal>)
}