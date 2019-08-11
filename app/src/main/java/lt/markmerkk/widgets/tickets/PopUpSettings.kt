package lt.markmerkk.widgets.tickets

import com.jfoenix.svg.SVGGlyph
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.widgets.PopUpAction
import lt.markmerkk.widgets.PopUpDisplay
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
                                    find<AccountSettingsWidget>().openModal(
                                            stageStyle = StageStyle.UTILITY,
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