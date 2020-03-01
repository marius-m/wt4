package lt.markmerkk.widgets.log_check

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TableView
import lt.markmerkk.Main
import lt.markmerkk.Styles
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.LogFormatters
import tornadofx.*
import javax.inject.Inject

class LogFreshnessWidget: Fragment() {

    @Inject lateinit var logFreshnessChecker: LogFreshnessChecker
    @Inject lateinit var syncInteractor: SyncInteractor

    init {
        Main.component().inject(this)
    }

    private lateinit var viewLogs: TableView<UnsyncLogViewModel>

    private val unsyncLogViewModels = mutableListOf<UnsyncLogViewModel>()
            .asObservable()

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Un-sync logs") {
                    addClass(Styles.dialogHeader)
                }
            }
        }
        center {
            vbox {
                label {
                    text = "There still are un-synchronized logs for this month, would you like to " +
                            "synchronize with JIRA to keep them up to date?"
                    isWrapText = true
                }
                viewLogs = tableview(unsyncLogViewModels) {
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    column("Date", UnsyncLogViewModel::dateProperty) {
                        minWidth = 90.0
                        maxWidth = 90.0
                    }
                    column("Duration", UnsyncLogViewModel::durationProperty) {
                        minWidth = 60.0
                        maxWidth = 60.0
                    }
                    column("Ticket", UnsyncLogViewModel::ticketProperty) {
                        minWidth = 80.0
                        maxWidth = 80.0
                    }
                    column("Comment", UnsyncLogViewModel::commentProperty)
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Synchronize".toUpperCase()) {
                    setOnAction {
                        syncInteractor.syncLogs(
                                logFreshnessChecker.firstDayOfMonth(),
                                logFreshnessChecker.lastDayOfMonth()
                        )
                        close()
                    }
                }
                jfxButton("Ignore and close app".toUpperCase()) {
                    setOnAction {
                        close()
                        Platform.exit()
                    }
                }
                jfxButton("Cancel".toUpperCase()) {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        val unsyncedLogs = logFreshnessChecker.unSynchronizedLogs()
                .map {
                    UnsyncLogViewModel(
                            ticket = it.code.code,
                            comment = it.comment,
                            duration = LogFormatters.humanReadableDurationShort(it.time.duration),
                            date = it.time.start.toLocalDate()
                    )
                }.sortedBy { it.date }
        unsyncLogViewModels.clear()
        unsyncLogViewModels.addAll(unsyncedLogs)
    }

}