package lt.markmerkk.export

import lt.markmerkk.*
import lt.markmerkk.entities.Log
import lt.markmerkk.export.entities.ExportWorklogViewModel
import lt.markmerkk.utils.LogFormatters
import org.joda.time.Duration
import org.slf4j.LoggerFactory

class ImportPresenter(
        private val worklogStorage: WorklogStorage,
        private val dayProvider: DayProvider,
        private val worklogExporter: WorklogExporter,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider
): ImportContract.Presenter {

    override val defaultProjectFilter: String = PROJECT_FILTER_ALL
    private var view: ImportContract.View? = null
    private var importWorklogs: List<Log> = emptyList()

    override fun onAttach(view: ImportContract.View) {
        this.view = view
    }

    override fun onDetach() { }

    override fun loadWorklogs(importWorklogs: List<Log>, projectFilter: String) {
        this.importWorklogs = importWorklogs
        val filterWorklogs = applyFilter(importWorklogs, projectFilter)
        val viewModels = filterWorklogs
                .map { ExportWorklogViewModel(it, includeDate = true) }
        view?.showWorklogs(viewModels)
        view?.showTotal(calcTotal(filterWorklogs))
    }

    override fun filterWorklogs(projectFilter: String) {
        val filterWorklogs = applyFilter(importWorklogs, projectFilter)
        val viewModels = filterWorklogs
                .map { ExportWorklogViewModel(it, includeDate = true) }
        view?.showWorklogs(viewModels)
        view?.showTotal(calcTotal(filterWorklogs))
    }

    override fun loadProjectFilters(worklogs: List<Log>) {
        val filterWithBoundTickets = worklogs.map {
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

    override fun import(
            worklogViewModels: List<ExportWorklogViewModel>,
            skipTicketCode: Boolean
    ) {
        val importWorklogs = if (skipTicketCode) {
            worklogViewModels
                    .filter { it.selectedProperty.get() }
                    .map { it.log.clearTicketCode() }
        } else {
            worklogViewModels
                    .filter { it.selectedProperty.get() }
                    .map { it.log }
        }
        importWorklogs
                .forEach { logStorage.insert(it.toLegacyLog(timeProvider)) }
        view?.showImportSuccess()
    }

    private fun applyFilter(worklogs: List<Log>, projectFilter: String): List<Log> {
        return when (projectFilter) {
            PROJECT_FILTER_ALL -> {
                worklogs.sortedBy { it.time.start }
            }
            PROJECT_FILTER_NOT_BOUND -> {
                worklogs.filter { it.code.codeProject.isEmpty() }
                        .sortedBy { it.time.start }
            }
            else -> {
                worklogs.filter { it.code.codeProject == projectFilter }
                        .sortedBy { it.time.start }
            }
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
        private val logger = LoggerFactory.getLogger(ImportPresenter::class.java)!!
        const val PROJECT_FILTER_ALL = "All"
        const val PROJECT_FILTER_NOT_BOUND = "Not assigned"
    }

}