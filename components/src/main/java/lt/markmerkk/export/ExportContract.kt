package lt.markmerkk.export

import lt.markmerkk.export.entities.ExportWorklogViewModel

interface ExportContract {
    interface View {
        fun showWorklogsForExport(worklogViewModels: List<ExportWorklogViewModel>)
        fun showExportSample(sampleAsString: String)
        fun showExportSuccess()
        fun showExportFailure()
    }

    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun load()
        fun sampleExport(worklogViewmodels: List<ExportWorklogViewModel>)
        fun exportWorklogs(worklogViewModels: List<ExportWorklogViewModel>)
    }
}