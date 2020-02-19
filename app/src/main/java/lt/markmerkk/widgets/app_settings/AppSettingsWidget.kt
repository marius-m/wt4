package lt.markmerkk.widgets.app_settings

import com.jfoenix.controls.JFXComboBox
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import lt.markmerkk.Main
import lt.markmerkk.Styles
import lt.markmerkk.UserSettings
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditAction
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxCombobox
import lt.markmerkk.utils.ConfigSetSettings
import tornadofx.*
import javax.inject.Inject

class AppSettingsWidget : Fragment() {

    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var configSetSettings: ConfigSetSettings

    init {
        Main.component().inject(this)
    }

    private lateinit var viewConfigProfile: JFXComboBox<String>
    private lateinit var viewCheckAutoStartClock: CheckBox
    private lateinit var viewCheckAutoSync: CheckBox

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("App settings") {
                    addClass(Styles.dialogHeader)
                }
            }
        }
        center {
            form {
                fieldset {
                    field {
                        vbox(spacing = 2) {
                            viewConfigProfile = jfxCombobox(
                                    SimpleStringProperty(configSetSettings.currentConfigOrDefault()),
                                    configSetSettings.configs()
                            ) {
                                isEditable = true
                            }
                            text("Change active profile (using multiple JIRAS). Needs restart to take effect!") {
                                addClass(Styles.labelMini)
                            }
                        }
                    }
                    field {
                        vbox(spacing = 2) {
                            viewCheckAutoStartClock = checkbox("Auto-start clock") {
                                isSelected = userSettings.settingsAutoStartClock
                            }
                            text("Clock starts whenever you log your time") {
                                addClass(Styles.labelMini)
                            }
                        }
                    }
                    field {
                        vbox(spacing = 2) {
                            viewCheckAutoSync = checkbox("Auto-sync") {
                                isSelected = userSettings.settingsAutoSync
                            }
                            text("Synchronization will start automatically") {
                                addClass(Styles.labelMini)
                            }
                        }
                    }
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Save".toUpperCase()) {
                    setOnAction {
                        userSettings.settingsAutoStartClock = viewCheckAutoStartClock.isSelected
                        userSettings.settingsAutoSync = viewCheckAutoSync.isSelected
                        configSetSettings.changeActiveConfig(viewConfigProfile.selectedItem ?: "")
                        configSetSettings.save()
                        close()
                    }
                }
                jfxButton("Close".toUpperCase()) {
                    setOnAction { close() }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        viewConfigProfile.selectionModel.select(configSetSettings.currentConfigOrDefault())
    }

    override fun onUndock() {
        super.onUndock()
    }

}