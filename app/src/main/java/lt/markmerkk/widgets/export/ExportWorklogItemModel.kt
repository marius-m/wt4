package lt.markmerkk.widgets.export

import lt.markmerkk.widgets.export.entities.ExportWorklogViewModel
import tornadofx.*

class ExportWorklogItemModel: ItemViewModel<ExportWorklogViewModel>() {
    val logAsString = bind(ExportWorklogViewModel::logAsStringProperty)
    val date = bind(ExportWorklogViewModel::dateProperty)
    val duration = bind(ExportWorklogViewModel::durationProperty)
    val ticket = bind(ExportWorklogViewModel::ticketProperty)
    val comment = bind(ExportWorklogViewModel::commentProperty)
    val selected = bind(ExportWorklogViewModel::selectedProperty, autocommit = true)
}