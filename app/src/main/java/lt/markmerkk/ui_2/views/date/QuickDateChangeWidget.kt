package lt.markmerkk.ui_2.views.date

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyph
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import lt.markmerkk.*
import lt.markmerkk.datepick.DateSelectResult
import lt.markmerkk.datepick.DateSelectType
import lt.markmerkk.entities.TimeRangeRaw.Companion.withEndTime
import lt.markmerkk.entities.TimeRangeRaw.Companion.withStartTime
import lt.markmerkk.events.EventChangeDate
import lt.markmerkk.events.EventChangeTime
import lt.markmerkk.timeselect.entities.TimeSelectType
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.widgets.datepicker.DatePickerWidget
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class QuickDateChangeWidget: Fragment(), DateChangeContract.View {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var resultDispatcher: ResultDispatcher

    init {
        Main.component().inject(this)
    }

    private lateinit var presenter: DateChangeContract.Presenter

    private lateinit var viewArrowLeft: JFXButton
    private lateinit var viewArrowRight: JFXButton
    private lateinit var viewButtonDate: JFXButton

    override val root: Parent = hbox {
        style {
            backgroundColor.add(Paint.valueOf(MaterialColors.LIGHTEST))
            backgroundRadius.add(box(20.px))
        }
        viewArrowLeft = jfxButton {
            graphic = graphics.from(Glyph.ARROW_LEFT, Color.BLACK, 6.0, 8.0)
            setOnAction { presenter.onClickPrev() }
        }
        viewButtonDate = jfxButton("Choose date") {
            setOnAction { presenter.onClickDate() }
        }
        viewArrowRight = jfxButton {
            graphic = graphics.from(Glyph.ARROW_RIGHT, Color.BLACK, 6.0, 8.0)
            setOnAction { presenter.onClickNext() }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter = QuickDateChangeWidgetPresenterDefault(
            resultDispatcher,
            logStorage
        )
        presenter.onAttach(this)
        eventBus.register(this)
    }

    override fun onUndock() {
        eventBus.register(this)
        presenter.onDetach()
        super.onUndock()
    }

    override fun onAttach() {
        presenter.onAttach(this)
    }

    override fun onDetach() {
        presenter.onDetach()
    }

    override fun render(title: String) {
        viewButtonDate.text = title
    }

    //region Events

    @Subscribe
    fun eventChangeDate(event: EventChangeDate) {
        val dateSelectResult = resultDispatcher.peek(
            DatePickerWidget.RESULT_DISPATCH_KEY_RESULT,
            DateSelectResult::class.java
        )
        if (dateSelectResult != null) {
            val dateSelectType = DateSelectType.fromRaw(dateSelectResult.extra)
            when (dateSelectType) {
                DateSelectType.TARGET_DATE -> {
                    presenter.selectDate(dateSelectResult.dateSelectionNew)
                    resultDispatcher.consume(DatePickerWidget.RESULT_DISPATCH_KEY_RESULT)
                }
                DateSelectType.UNKNOWN,
                DateSelectType.SELECT_FROM,
                DateSelectType.SELECT_TO -> {}
            }.javaClass
        }
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.MAIN)
    }

}