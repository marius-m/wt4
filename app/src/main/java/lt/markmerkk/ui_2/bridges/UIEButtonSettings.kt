package lt.markmerkk.ui_2.bridges

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXPopup
import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.Strings
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.ProfilesDialog
import lt.markmerkk.ui_2.SettingsDialog
import lt.markmerkk.ui_2.StatisticsDialog

/**
 * Represents settings button, its graphics, animations
 */
class UIEButtonSettings(
        private val graphics: Graphics<SVGGlyph>,
        private val strings: Strings,
        private val externalSourceNode: ExternalSourceNode<StackPane>,
        private val button: JFXButton,
        private val eventBus: EventBus
) : UIElement<JFXButton> {

    private var jfxPopup: JFXPopup = JFXPopup()

    private val labelStatistics = Label(
            strings.getString("ui_button_settings_total"),
            graphics.from(Glyph.STATISTICS, Color.BLACK, 20.0)
    )
    private val labelSettings = Label(
            strings.getString("ui_button_settings_settings"),
            graphics.from(Glyph.SETTINGS, Color.BLACK, 20.0)
    )
    private val labelProfiles = Label(
            strings.getString("ui_button_settings_profiles"),
            graphics.from(Glyph.ACCOUNT, Color.BLACK, 20.0)
    )

    private val labelTest = Label(
            strings.getString("ui_button_settings_test"),
            graphics.from(Glyph.ACCOUNT, Color.BLACK, 20.0)
    )

    init {
        button.graphic = graphics.from(Glyph.SETTINGS, Color.WHITE, 20.0)
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
        labelsList.items.add(labelProfiles)
        labelsList.items.add(labelStatistics)
        labelsList.items.add(labelSettings)
        labelsList.items.add(labelTest)
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
            labelSettings -> {
                val dialog = SettingsDialog()
                val jfxDialog = dialog.view as JFXDialog
                jfxDialog.show(externalSourceNode.rootNode())
                jfxDialog.setOnDialogClosed { InjectorNoDI.forget(dialog) }
            }
            labelProfiles -> {
                val dialog = ProfilesDialog()
                val jfxDialog = dialog.view as JFXDialog
                jfxDialog.show(externalSourceNode.rootNode())
                jfxDialog.setOnDialogClosed { InjectorNoDI.forget(dialog) }
            }
            labelTest -> {
                eventBus.post(EventInflateDialog(DialogType.TICKET_SEARCH))
            }
            else -> throw IllegalStateException("Cannot define selected label")
        }
    }

    //endregion

}