package lt.markmerkk.export

import lt.markmerkk.entities.Log
import lt.markmerkk.export.entities.ExportWorklogViewModel

interface ImportContract {
    interface View {
        fun showWorklogs(worklogViewModels: List<ExportWorklogViewModel>)
        fun showProjectFilters(projectFilters: List<String>, filterSelection: String)
        fun showImportSuccess()
        fun showTotal(totalAsString: String)
    }
    interface Presenter {
        val defaultProjectFilter: String

        fun onAttach(view: View)
        fun onDetach()
        fun loadWorklogs(importWorklogs: List<Log>, projectFilter: String)

        fun filterClear(projectFilter: String)
        fun filterWorklogsWithCodeFromComment(projectFilter: String)
        fun filterWorklogsWithCodeAndRemoveFromComment(projectFilter: String)
        fun filterWorklogsNoCode(projectFilter: String)

        fun import(
                worklogViewModels: List<ExportWorklogViewModel>,
                skipTicketCode: Boolean
        )
    }
}