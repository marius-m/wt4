package lt.markmerkk.widgets.tickets

import com.jfoenix.svg.SVGGlyph
import javafx.scene.Node
import javafx.scene.paint.Color
import lt.markmerkk.DisplayType
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventChangeDisplayType
import lt.markmerkk.widgets.PopUpAction
import lt.markmerkk.widgets.PopUpDisplay

class PopUpChangeMainContent(
        private val graphics: Graphics<SVGGlyph>,
        private val eventBus: WTEventBus,
        private val attachTo: Node
) : PopUpDisplay {

    override fun show() {
        createPopUpDisplay(
                actions = listOf(
                        PopUpAction(
                                title = "Day in calendar",
                                graphic = graphics.from(Glyph.DISPLAY_DAY, Color.BLACK, 12.0),
                                action = {
                                    eventBus.post(EventChangeDisplayType(DisplayType.CALENDAR_VIEW_DAY))
                                }
                        ),
                        PopUpAction(
                                title = "Week in calendar",
                                graphic = graphics.from(Glyph.DISPLAY_WEEK, Color.BLACK, 12.0),
                                action = {
                                    eventBus.post(EventChangeDisplayType(DisplayType.CALENDAR_VIEW_WEEK))
                                }
                        ),
                        PopUpAction(
                                title = "Day as list",
                                graphic = graphics.from(Glyph.DISPLAY_LIST, Color.BLACK, 12.0, 10.0),
                                action = {
                                    eventBus.post(EventChangeDisplayType(DisplayType.TABLE_VIEW_DETAIL))
                                }
                        )
                ),
                attachTo = attachTo
        )
    }
}