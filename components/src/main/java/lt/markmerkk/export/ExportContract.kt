package lt.markmerkk.export

import lt.markmerkk.export.entities.ExportWorklogViewModel

interface ExportContract {
    interface View {
        fun showWorklogsForExport(worklogViewModels: List<ExportWorklogViewModel>)
        fun showProjectFilters(projectFilters: List<String>, filterSelection: String)
        fun showExportSample(sampleAsString: String)
        fun showExportSuccess()
        fun showExportFailure()
    }

    interface Presenter {
        val defaultProjectFilter: String

        fun onAttach(view: View)
        fun onDetach()
        fun loadWorklogs(projectFilter: String)
        fun loadProjectFilters()
        fun sampleExport(worklogViewModels: List<ExportWorklogViewModel>)
        fun exportWorklogs(worklogViewModels: List<ExportWorklogViewModel>)
    }
}