package lt.markmerkk.widgets.list

import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import tornadofx.*

class ListLogStatusIndicatorCell: TableCellFragment<ListLogWidget.LogViewModel, String>() {

    private lateinit var viewStatusIndicator: Circle

    override val root: Parent = stackpane {
        viewStatusIndicator = circle {
            centerX = 12.0
            centerY = 12.0
            radius = 10.0
        }
    }

    override fun onDock() {
        super.onDock()
        viewStatusIndicator.fill = Paint.valueOf(item)
    }

}