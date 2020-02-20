package lt.markmerkk.widgets.tickets

import com.jfoenix.svg.SVGGlyph
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.BuildConfig
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.widgets.HelpWidget
import lt.markmerkk.widgets.PopUpAction
import lt.markmerkk.widgets.PopUpDisplay
import lt.markmerkk.widgets.app_settings.AppSettingsWidget
import lt.markmerkk.widgets.credits.CreditsWidget
import lt.markmerkk.widgets.export.ExportWidget
import lt.markmerkk.widgets.settings.AccountSettingsOauthWidget
import lt.markmerkk.widgets.settings.AccountSettingsWidget
import lt.markmerkk.widgets.statistics.StatisticsWidget
import tornadofx.*

class PopUpSettings(
    private val graphics: Graphics<SVGGlyph>,
    private val attachTo: Node,
    private val hostServicesInteractor: HostServicesInteractor
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
                                                stageStyle = StageStyle.DECORATED,
                                                modality = Modality.APPLICATION_MODAL,
                                                block = false,
                                                resizable = true
                                        )
                                    } else {
                                        find<AccountSettingsWidget>().openModal(
                                                stageStyle = StageStyle.DECORATED,
                                                modality = Modality.APPLICATION_MODAL,
                                                block = false,
                                                resizable = true
                                        )
                                    }
                                }
                        ),
                        PopUpAction(
                                title = "App settings",
                                graphic = graphics.from(Glyph.SETTINGS2, Color.BLACK, 12.0),
                                action = {
                                    find<AppSettingsWidget>().openModal(
                                            stageStyle = StageStyle.DECORATED,
                                            modality = Modality.APPLICATION_MODAL,
                                            block = false,
                                            resizable = true
                                    )
                                }
                        ),
                        PopUpAction(
                                title = "Statistics",
                                graphic = graphics.from(Glyph.STATISTICS, Color.BLACK, 12.0),
                                action = {
                                    find<StatisticsWidget>().openModal(
                                            stageStyle = StageStyle.DECORATED,
                                            modality = Modality.APPLICATION_MODAL,
                                            block = false,
                                            resizable = true
                                    )
                                }
                        ),
                        PopUpAction(
                                title = "Export worklogs",
                                graphic = graphics.from(Glyph.IMPORT_EXPORT, Color.BLACK, 12.0),
                                action = {
                                    find<ExportWidget>().openModal(
                                            stageStyle = StageStyle.DECORATED,
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
                                            stageStyle = StageStyle.DECORATED,
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
                                    hostServicesInteractor.openLink("https://github.com/marius-m/wt4/wiki")
                                }
                        )
                ),
                attachTo = attachTo
        )
    }
}