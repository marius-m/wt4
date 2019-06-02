package lt.markmerkk.tickets

import lt.markmerkk.Mocks
import lt.markmerkk.entities.Ticket

object MocksTickets {

    val tickets: List<Ticket> = listOf(
            "google",           // TTS-001
            "bing",             // TTS-002
            "facebook",         // TTS-003
            "linkedin",         // TTS-004
            "twitter",          // TTS-005
            "googleplus",       // TTS-006
            "bingnews",         // TTS-007
            "plexoogl"          // TTS-008
    ).mapIndexed { index: Int, description: String ->
        Mocks.createTicket(code = "TTS-11${index + 1}", description = description)
    }
}