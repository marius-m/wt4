package lt.markmerkk.export

import lt.markmerkk.DayProvider
import lt.markmerkk.WorklogStorage
import lt.markmerkk.export.entities.ExportWorklogViewModel
import lt.markmerkk.utils.LogFormatters
import org.slf4j.LoggerFactory

class ExportPresenter(
        private val worklogStorage: WorklogStorage,
        private val dayProvider: DayProvider,
        private val worklogExporter: WorklogExporter
): ExportContract.Presenter {

    private var view: ExportContract.View? = null

    override fun onAttach(view: ExportContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun load() {
        val worklogsForExport = worklogStorage.loadWorklogsSync(
                from = dayProvider.startAsDate(),
                to = dayProvider.endAsDate()
        )
        val hasMultipleDates = LogFormatters.hasMultipleDates(worklogsForExport)
        val worklogViewModels = worklogsForExport
                .sortedBy { it.time.start }
                .map { ExportWorklogViewModel(it, hasMultipleDates) }
        view?.showWorklogsForExport(worklogViewModels)
    }

    override fun sampleExport(worklogViewModels: List<ExportWorklogViewModel>) {
        val logs = worklogViewModels
                .filter { it.selectedProperty.get() }
                .map { it.log }
        val logsAsString = LogFormatters.formatLogsBasic(logs)
        view?.showExportSample(logsAsString)
    }

    override fun exportWorklogs(worklogViewModels: List<ExportWorklogViewModel>) {
        val logsForExport = worklogViewModels
                .filter { it.selectedProperty.get() }
                .map { it.log }
        logger.debug("Exporting $logsForExport")
        val isExportSuccess = worklogExporter.exportToFile(logsForExport)
        if (isExportSuccess) {
            view?.showExportSuccess()
        } else {
            view?.showExportFailure()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExportPresenter::class.java)!!
    }

}