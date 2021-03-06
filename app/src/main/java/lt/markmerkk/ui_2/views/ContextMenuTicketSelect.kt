package lt.markmerkk.ui_2.views

import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import com.vdurmont.emoji.EmojiParser
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.events.EventSnackBarMessage
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.utils.AccountAvailablility
import lt.markmerkk.utils.JiraLinkGenerator
import lt.markmerkk.utils.JiraLinkGeneratorBasic
import lt.markmerkk.utils.JiraLinkGeneratorOAuth
import lt.markmerkk.widgets.tickets.TicketViewModel
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscription

class ContextMenuTicketSelect(
        private val graphics: Graphics<SVGGlyph>,
        private val eventBus: WTEventBus,
        private val hostServicesInteractor: HostServicesInteractor,
        private val accountAvailablility: AccountAvailablility
) {

    private val jiraLinkGenerator = if (BuildConfig.oauth) {
        JiraLinkGeneratorOAuth(view = null, accountAvailability = accountAvailablility)
    } else {
        JiraLinkGeneratorBasic(view = null, accountAvailablility = accountAvailablility)
    }

    private var subsTicketSelect: Subscription? = null

    fun onAttach() {}
    fun onDetach() {
        subsTicketSelect?.unsubscribe()
    }

    fun attachTicketSelection(ticketCodeSelectAsStream: Observable<String?>) {
        ticketCodeSelectAsStream
                .filter { it != null }
                .subscribe({
                    if (it != null) {
                        bindCodes(listOf(it))
                    } else {
                        bindCodes(emptyList())
                    }
                }, { error ->
                    logger.warn("Error subscribing to ticket code selection stream", error)
                })
    }

    val root: ContextMenu = ContextMenu()
            .apply {
                items.addAll(
                        MenuItem(
                                "Copy web-link",
                                graphics.from(Glyph.LINK, Color.BLACK, 14.0, 16.0)
                        ).apply { id = SelectType.WEB_LINK.name },
                        MenuItem(
                                "Open in browser",
                                graphics.from(Glyph.NEW, Color.BLACK, 16.0, 16.0)
                        ).apply { id = SelectType.BROWSER.name }
                )
                setOnAction { event ->
                    val selectType = SelectType.valueOf((event.target as MenuItem).id)
                    val ticketCode = TicketCode.new(selectedCodes.firstOrNull() ?: "")
                    if (!ticketCode.isEmpty()) {
                        when (selectType) {
                            SelectType.WEB_LINK -> {
                                val webLinkToTicket = jiraLinkGenerator.webLinkFromInput(ticketCode.code)
                                val message = EmojiParser.parseToUnicode("Copied $webLinkToTicket :rocket:")
                                eventBus.post(EventSnackBarMessage(message))
                                hostServicesInteractor.ticketWebLinkToClipboard(webLinkToTicket)
                            }
                            SelectType.BROWSER -> {
                                val webLinkToTicket = jiraLinkGenerator.webLinkFromInput(ticketCode.code)
                                hostServicesInteractor.openLink(webLinkToTicket)
                            }
                        }
                    }
                }
            }
    private var selectedCodes = emptyList<String>()

    fun bindCodes(codes: List<String>) {
        selectedCodes = codes
    }

    private enum class SelectType {
        WEB_LINK,
        BROWSER,
        ;
    }

    companion object {
        val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}