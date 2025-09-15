package lt.markmerkk.widgets.app_settings

import com.jfoenix.controls.JFXComboBox
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import lt.markmerkk.Main
import lt.markmerkk.Styles
import lt.markmerkk.UserSettings
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxCombobox
import lt.markmerkk.utils.ConfigSetSettings
import tornadofx.*
import javax.inject.Inject
import lt.markmerkk.ui_2.BaseFragment

class ProfileWidget: BaseFragment() {

    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var configSetSettings: ConfigSetSettings

    init {
        Main.component().inject(this)
    }

    private lateinit var viewConfigProfile: JFXComboBox<String>


    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Profiles") {
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
                            text("Change active profile (using multiple JIRA). Needs restart to take effect!") {
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
                jfxButton("Change profile".toUpperCase()) {
                    setOnAction {
                        configSetSettings.changeActiveConfig(viewConfigProfile.selectedItem ?: "")
                        configSetSettings.save()
                        close()
                        Main.restart()
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
}