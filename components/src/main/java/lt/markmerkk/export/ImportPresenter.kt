package lt.markmerkk.export

import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Log.Companion.cloneAsNewLocal
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.export.entities.ExportWorklogViewModel
import lt.markmerkk.utils.LogFormatters
import org.joda.time.Duration
import org.slf4j.LoggerFactory

class ImportPresenter(
    private val activeDisplayRepository: ActiveDisplayRepository,
    private val timeProvider: TimeProvider,
) : ImportContract.Presenter {

    override val defaultProjectFilter: String = PROJECT_FILTER_ALL
    private var view: ImportContract.View? = null
    private var worklogsImported: List<Log> = emptyList()
    private var worklogsModified: List<Log> = emptyList()

    override fun onAttach(view: ImportContract.View) {
        this.view = view
    }

    override fun onDetach() { }

    override fun loadWorklogs(importWorklogs: List<Log>, projectFilter: String) {
        this.worklogsImported = importWorklogs
        this.worklogsModified = worklogsImported
            .applyProjectFilter(projectFilter = projectFilter)
        val viewModels = worklogsModified
                .map { ExportWorklogViewModel(it, includeDate = true) }
        val selectableTicketFilters = loadProjectFilters(worklogsImported)
        view?.showWorklogs(viewModels)
        view?.showProjectFilters(selectableTicketFilters, defaultProjectFilter)
        view?.showTotal(calcTotal(worklogsModified))
    }

    override fun filterWorklogsByProject(projectFilter: String) {
        this.worklogsModified = worklogsImported.applyProjectFilter(projectFilter)
        val viewModels = worklogsModified
                .map { ExportWorklogViewModel(it, includeDate = true) }
        view?.showWorklogs(viewModels)
        view?.showTotal(calcTotal(worklogsModified))
    }

    override fun filterWorklogsWithCodeFromComment() {
        this.worklogsModified = worklogsImported
            .applyTicketCodeFromComment(timeProvider)
        val viewModels = worklogsModified
            .map { ExportWorklogViewModel(it, includeDate = true) }
        view?.showWorklogs(viewModels)
        view?.showTotal(calcTotal(worklogsModified))
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
                .forEach { activeDisplayRepository.insertOrUpdate(it) }
        view?.showImportSuccess()
    }

    /**
     * Loads project filters (selections of possible ticket codes)
     */
    private fun loadProjectFilters(worklogs: List<Log>): List<String> {
        val filterWithBoundTickets = worklogs.map {
            val projectCode = it.code.codeProject
            if (projectCode.isEmpty()) {
                PROJECT_FILTER_NOT_BOUND
            } else {
                projectCode
            }
        }.filterNot { it == PROJECT_FILTER_NOT_BOUND }
            .toSet()
        return listOf(PROJECT_FILTER_ALL, PROJECT_FILTER_NOT_BOUND)
            .plus(filterWithBoundTickets)
    }

    private fun calcTotal(worklogs: List<Log>): String {
        val totalDuration = worklogs.map { it.time.duration }
                .fold(Duration(0)) { acc, next ->
                    acc.plus(next)
                }
        return LogFormatters.humanReadableDuration(totalDuration)
    }

    companion object {
        private val l = LoggerFactory.getLogger(ImportPresenter::class.java)!!
        const val PROJECT_FILTER_ALL = "All"
        const val PROJECT_FILTER_NOT_BOUND = "Not assigned"

        internal fun List<Log>.applyProjectFilter(projectFilter: String): List<Log> {
            return when (projectFilter) {
                PROJECT_FILTER_ALL -> {
                    this.sortedBy { it.time.start }
                }
                PROJECT_FILTER_NOT_BOUND -> {
                    this.filter { it.code.codeProject.isEmpty() }
                        .sortedBy { it.time.start }
                }
                else -> {
                    this.filter { it.code.codeProject == projectFilter }
                        .sortedBy { it.time.start }
                }
            }
        }

        internal fun Log.extractTicketCodeFromComment(): TicketCode {
            return TicketCode.new(this.comment)
        }

        internal fun List<Log>.applyTicketCodeFromComment(
            timeProvider: TimeProvider,
        ): List<Log> {
            return this.map { originalLog ->
                originalLog.cloneAsNewLocal(
                    timeProvider = timeProvider,
                    code = originalLog.extractTicketCodeFromComment()
                )
            }
        }
    }

}