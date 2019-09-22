package lt.markmerkk.widgets.tickets

import com.jfoenix.svg.SVGGlyph
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.BuildConfig
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.widgets.HelpWidget
import lt.markmerkk.widgets.PopUpAction
import lt.markmerkk.widgets.PopUpDisplay
import lt.markmerkk.widgets.credits.CreditsWidget
import lt.markmerkk.widgets.settings.AccountSettingsOauthWidget
import lt.markmerkk.widgets.settings.AccountSettingsWidget
import lt.markmerkk.widgets.statistics.StatisticsWidget
import tornadofx.*

class PopUpSettings(
    private val graphics: Graphics<SVGGlyph>,
    private val attachTo: Node
): PopUpDisplay {

    override fun show() {
        createPopUpDisplay(
                actions = listOf(
                        PopUpAction(
                                title = "Account settings",
                                graphic = graphics.from(Glyph.ACCOUNT, Color.BLACK, 12.0),
                                action = {
                                    if (BuildConfig.oauth) {
                                        find<AccountSettingsOauthWidget>().openModal(
                                                stageStyle = StageStyle.UTILITY,
                                                modality = Modality.APPLICATION_MODAL,
                                                block = false,
                                                resizable = true
                                        )
                                    } else {
                                        find<AccountSettingsWidget>().openModal(
                                                stageStyle = StageStyle.UTILITY,
                                                modality = Modality.APPLICATION_MODAL,
                                                block = false,
                                                resizable = true
                                        )
                                    }
                                }
                        ),
                        PopUpAction(
                                title = "Statistics",
                                graphic = graphics.from(Glyph.STATISTICS, Color.BLACK, 12.0),
                                action = {
                                    find<StatisticsWidget>().openModal(
                                            stageStyle = StageStyle.UTILITY,
                                            modality = Modality.APPLICATION_MODAL,
                                            block = false,
                                            resizable = true
                                    )
                                }
                        ),
                        PopUpAction(
                                title = "Tickets",
                                graphic = graphics.from(Glyph.PAGE, Color.BLACK, 10.0, 12.0),
                                action = {
                                    find<TicketWidget>().openModal(
                                            stageStyle = StageStyle.UTILITY,
                                            modality = Modality.APPLICATION_MODAL,
                                            block = false,
                                            resizable = true
                                    )
                                }
                        ),
                        PopUpAction(
                                title = "Credits",
                                graphic = graphics.from(Glyph.HELP, Color.BLACK, 12.0, 12.0),
                                action = {
                                    find<CreditsWidget>().openModal(
                                            stageStyle = StageStyle.UTILITY,
                                            modality = Modality.APPLICATION_MODAL,
                                            block = false,
                                            resizable = true
                                    )
                                }
                        ),
                        PopUpAction(
                                title = "Help",
                                graphic = graphics.from(Glyph.HELP, Color.BLACK, 12.0, 12.0),
                                action = {
                                    find<HelpWidget>().openModal(
                                            stageStyle = StageStyle.UTILITY,
                                            modality = Modality.APPLICATION_MODAL,
                                            block = false,
                                            resizable = true
                                    )
                                }
                        )
                ),
                attachTo = attachTo
        )
    }
}