package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXPopup
import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.Main
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.CurrentDayDialog
import lt.markmerkk.ui_2.StatisticsDialog

/**
 * Represents settings button, its graphics, animations
 */
class UIEButtonSettings(
        private val externalSourceNode: ExternalSourceNode<StackPane>,
        private val button: JFXButton,
        private val syncInteractor: SyncInteractor
) : UIElement<JFXButton> {

    private val glyphSettings: SVGGlyph = settingsGlyph(Color.WHITE, 20.0)
    private val glyphStatistics: SVGGlyph = statisticsGlyph(Color.BLACK, 20.0)
    private val glyphPaint: SVGGlyph = paintGlyph(Color.BLACK, 20.0)
    private val glyphRefresh: SVGGlyph = refreshGlyph(Color.BLACK, 20.0, 16.0)
    private var jfxPopup: JFXPopup = JFXPopup()

    private val labelStatistics = Label("Total", glyphStatistics)
    private val labelRefresh = Label("Force refresh", glyphRefresh)
    private val labelBackToDefault = Label("Default view", glyphPaint)

    init {
        button.graphic = glyphSettings
        button.setOnMouseClicked {
            jfxPopup = JFXPopup(createSelectionList())
            jfxPopup.isAutoFix = true
            jfxPopup.show(button, JFXPopup.PopupVPosition.BOTTOM, JFXPopup.PopupHPosition.LEFT)
        }
    }

    override fun raw(): JFXButton = button

    override fun show() {}

    override fun hide() {}

    override fun reset() {}

    //region Convenience

    /**
     * Convenience method to create selection list
     */
    private fun createSelectionList(): Region {
        val labelsList = JFXListView<Label>()
        labelsList.items.add(labelStatistics)
        labelsList.items.add(labelRefresh)
        labelsList.items.add(labelBackToDefault)
        labelsList.selectionModel.selectedItemProperty().addListener(object : ChangeListener<Label> {
            override fun changed(observable: ObservableValue<out Label>?, oldValue: Label?, newValue: Label) {
                jfxPopup.hide()
                handleSelection(newValue)
            }
        })
        val itemsContainer = StackPane(labelsList)
        itemsContainer.minWidth = 200.0
        return itemsContainer
    }

    private fun handleSelection(selectLabel: Label) {
        when (selectLabel) {
            labelStatistics -> {
                val jfxDialog = StatisticsDialog().view as JFXDialog
                jfxDialog.show(externalSourceNode.rootNode())
                jfxDialog.setOnDialogClosed { InjectorNoDI.forget(jfxDialog) }
            }
            labelRefresh -> {
                syncInteractor.syncAll()
            }
            labelBackToDefault -> {
                Main.Companion.MATERIAL = false
                Main.Companion.mainInstance!!.restart()
            }
            else -> throw IllegalStateException("Cannot define selected label")
        }
    }

    //endregion

    //region Glyphs

    // todo : export hardcoded glyph
    private fun settingsGlyph(color: Color, size: Double): SVGGlyph {
        val svgGlyph = SVGGlyph(
                -1,
                "settings",
                "M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z",
                color
        )
        svgGlyph.setSize(size, size)
        return svgGlyph
    }

    private fun statisticsGlyph(color: Color, size: Double): SVGGlyph {
        val svgGlyph = SVGGlyph(
                -1,
                "statistics",
                "M10 20h4V4h-4v16zm-6 0h4v-8H4v8zM16 9v11h4V9h-4z",
                color
        )
        svgGlyph.setSize(size, size)
        return svgGlyph
    }

    private fun refreshGlyph(color: Color, width: Double, height: Double): SVGGlyph {
        val svgGlyph = SVGGlyph(
                -1,
                "refresh",
                "M19 8l-4 4h3c0 3.31-2.69 6-6 6-1.01 0-1.97-.25-2.8-.7l-1.46 1.46C8.97 19.54 10.43 20 12 20c4.42 0 8-3.58 8-8h3l-4-4zM6 12c0-3.31 2.69-6 6-6 1.01 0 1.97.25 2.8.7l1.46-1.46C15.03 4.46 13.57 4 12 4c-4.42 0-8 3.58-8 8H1l4 4 4-4H6z",
                color
        )
        svgGlyph.setSize(width, height)
        return svgGlyph
    }

    private fun paintGlyph(color: Color, size: Double): SVGGlyph {
        val svgGlyph = SVGGlyph(
                -1,
                "paint",
                "M7 14c-1.66 0-3 1.34-3 3 0 1.31-1.16 2-2 2 .92 1.22 2.49 2 4 2 2.21 0 4-1.79 4-4 0-1.66-1.34-3-3-3zm13.71-9.37l-1.34-1.34c-.39-.39-1.02-.39-1.41 0L9 12.25 11.75 15l8.96-8.96c.39-.39.39-1.02 0-1.41z",
                color
        )
        svgGlyph.setSize(size, size)
        return svgGlyph
    }


    //endregion

}