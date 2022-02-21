package lt.markmerkk.export

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.Log
import lt.markmerkk.export.entities.ExportWorklogViewModel
import lt.markmerkk.utils.LogFormatters
import org.joda.time.Duration
import org.slf4j.LoggerFactory

class ExportPresenter(
    private val worklogStorage: WorklogStorage,
    private val worklogExporter: WorklogExporter,
    private val activeDisplayRepository: ActiveDisplayRepository
) : ExportContract.Presenter {

    override val defaultProjectFilter: String = PROJECT_FILTER_ALL

    private var view: ExportContract.View? = null

    override fun onAttach(view: ExportContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun loadWorklogs(projectFilter: String) {
        val worklogsForExport = when (projectFilter) {
            PROJECT_FILTER_ALL -> {
                worklogStorage.loadWorklogsSync(
                        from = activeDisplayRepository.displayDateRange.start,
                        to = activeDisplayRepository.displayDateRange.endAsNextDay
                )
            }
            PROJECT_FILTER_NOT_BOUND -> {
                worklogStorage.loadWorklogsSync(
                        from = activeDisplayRepository.displayDateRange.start,
                        to = activeDisplayRepository.displayDateRange.endAsNextDay
                ).filter { it.code.codeProject.isEmpty() }
            }
            else -> {
                worklogStorage.loadWorklogsSync(
                        from = activeDisplayRepository.displayDateRange.start,
                        to = activeDisplayRepository.displayDateRange.endAsNextDay
                ).filter { it.code.codeProject == projectFilter }
            }
        }
        val hasMultipleDates = LogFormatters.hasMultipleDates(worklogsForExport)
        val worklogViewModels = worklogsForExport
                .sortedBy { it.time.start }
                .map { ExportWorklogViewModel(it, hasMultipleDates) }
        view?.showWorklogsForExport(worklogViewModels)
        view?.showTotal(calcTotal(worklogsForExport))
    }

    override fun loadProjectFilters() {
        val filterWithBoundTickets = worklogStorage.loadWorklogsSync(
                from = activeDisplayRepository.displayDateRange.start,
                to = activeDisplayRepository.displayDateRange.endAsNextDay
        ).map {
            val projectCode = it.code.codeProject
            if (projectCode.isEmpty()) {
                PROJECT_FILTER_NOT_BOUND
            } else {
                projectCode
            }
        }.filterNot { it == PROJECT_FILTER_NOT_BOUND }
                .toSet()
        val projectFilters = listOf(PROJECT_FILTER_ALL, PROJECT_FILTER_NOT_BOUND)
                .plus(filterWithBoundTickets)
        view?.showProjectFilters(projectFilters, defaultProjectFilter)
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

    private fun calcTotal(worklogs: List<Log>): String {
        val totalDuration = worklogs.map { it.time.duration }
                .fold(Duration(0)) { acc, next ->
                    acc.plus(next)
                }
        return LogFormatters.humanReadableDuration(totalDuration)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExportPresenter::class.java)!!
        const val PROJECT_FILTER_ALL = "All"
        const val PROJECT_FILTER_NOT_BOUND = "Not assigned"
    }

}