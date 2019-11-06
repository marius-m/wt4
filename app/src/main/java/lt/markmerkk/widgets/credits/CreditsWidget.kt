package lt.markmerkk.widgets.credits

import javafx.collections.ListChangeListener
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.SelectionMode
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.text.Text
import lt.markmerkk.Main
import lt.markmerkk.SchedulerProvider
import lt.markmerkk.Tags
import lt.markmerkk.credits.CreditsContract
import lt.markmerkk.credits.CreditsPresenter
import lt.markmerkk.entities.Credit
import lt.markmerkk.repositories.CreditsRepository
import org.slf4j.LoggerFactory
import javax.inject.Inject
import tornadofx.*

class CreditsWidget(): Fragment(), CreditsContract.View {

    @Inject lateinit var creditsRepository: CreditsRepository
    @Inject lateinit var schedulerProvider: SchedulerProvider

    init {
        Main.component().inject(this)
    }

    private lateinit var viewTextTitle: Label
    private lateinit var viewTextDescription: Text
    private lateinit var viewTextLink: Label
    private lateinit var viewTextLicense: Label
    private lateinit var viewTextLicenseLink: Label
    private lateinit var viewTextLicenseFull: TextArea

    private val presenter = CreditsPresenter(
        creditsRepository = creditsRepository,
        ioScheduler = schedulerProvider.io(),
        uiScheduler = schedulerProvider.ui()
    )

    private val creditItems = listOf<String>()
        .toObservable()

    override val root: Parent = borderpane {
        left {
            listview(creditItems) {
                selectionModel.selectionMode = SelectionMode.SINGLE
                selectionModel.selectedItems.addListener(ListChangeListener<String> { change ->
                    presenter.findCreditDetails(selectedItem ?: "")
                })
            }
        }
        center {
            vbox(spacing = 4) {
                style {
                    padding = box(4.px)
                }
                minWidth = 400.0
                minHeight = 500.0
                form {
                    fieldset("Details") {
                        field("Title") {
                            viewTextTitle = label { addClass("h1") }
                        }
                        field("Link") {
                            viewTextLink = label { addClass("h4") }
                        }
                        field("License link") {
                            viewTextLicenseLink = label { addClass("h4") }
                        }
                        field("Description") {
                            viewTextDescription = text {
                                addClass("p")
                                wrappingWidth = 300.0
                            }
                        }
                        field("License") {
                            viewTextLicense = label { addClass("p") }
                        }
                    }
                }
                viewTextLicenseFull = textarea {
                    prefRowCount = 5
                    vgrow = Priority.ALWAYS
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        this.presenter.onAttach(this)
    }

    override fun onUndock() {
        super.onUndock()
        this.presenter.onDetach()
    }

    override fun renderCreditEntries(entries: List<String>) {
        creditItems.clear()
        creditItems.addAll(entries)
    }

    override fun showCreditDetails(entry: Credit) {
        viewTextTitle.text = entry.title
        viewTextDescription.text = entry.description
        viewTextLink.text = entry.link
        viewTextLicense.text = entry.license
        viewTextLicenseLink.text = entry.licenseLink
        viewTextLicenseFull.text = entry.licenseFull
    }

    companion object {
        val logger = LoggerFactory.getLogger(Tags.INTERNAL)!!
    }

}