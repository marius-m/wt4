package lt.markmerkk.ui.version

import com.sun.javafx.application.HostServicesDelegate
import javafx.application.Application
import javafx.application.HostServices
import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.ProgressIndicator
import javafx.scene.text.Text

import javax.annotation.PreDestroy
import javax.inject.Inject

import lt.markmerkk.Config
import lt.markmerkk.Main
import lt.markmerkk.Translation
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.mvp.VersioningMvp
import lt.markmerkk.ui.interfaces.DialogListener
import org.slf4j.LoggerFactory

/**
 * Created by mariusmerkevicius on 12/14/15. Represents the presenter to update the log
 */
class VersionPresenter : Initializable, VersioningMvp.View {

    @Inject
    lateinit var config: Config
    @Inject
    lateinit var application: Application
    @Inject
    lateinit var hostServicesInteractor: HostServicesInteractor

    @FXML lateinit var buttonClose: Button
    @FXML lateinit var buttonTitle: Hyperlink
    @FXML lateinit var buttonAuthor: Hyperlink
    @FXML lateinit var buttonPlace: Hyperlink
    @FXML lateinit var buttonUpdate: Hyperlink
    @FXML lateinit var labelVersion: Text
    @FXML lateinit var progressIndicator: ProgressIndicator

    private var dialogListener: DialogListener? = null

    override fun initialize(location: URL, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        labelVersion.text = String.format("Version: %s", config.versionName)
    }

    @PreDestroy
    fun destroy() {
    }

    //region Action input

    fun onClickClose() {
        dialogListener?.onCancel()
        InjectorNoDI.forget(this)
    }

    fun onClickSave() {
    }

    fun onClickTitle() {
        hostServicesInteractor.openExternalLink("https://bitbucket.org/mmerkevicius/wt4")
    }

    fun onClickAuthor() {
        hostServicesInteractor.openExternalLink("https://github.com/marius-m")
    }

    fun onClickPlace() {
        hostServicesInteractor.openExternalLink("https://www.treatwell.co.uk/")
    }

    fun onHyperlinkGoogle1() {
        hostServicesInteractor.openExternalLink("https://design.google.com/icons/")

    }

    fun onHyperlinkGoogle2() {
        hostServicesInteractor.openExternalLink("https://creativecommons.org/licenses/by/4.0/")
    }

    fun onHyperlinkJFX1() {
        hostServicesInteractor.openExternalLink("https://github.com/JFXtras/jfxtras")
    }

    fun onHyperlinkJFX2() {
        hostServicesInteractor.openExternalLink("https://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22Revised_BSD_License.22.2C_.22New_BSD_License.22.2C_or_.22Modified_BSD_License.22.29")
    }

    fun onHyperlinkOthers() {
        hostServicesInteractor.openExternalLink("https://github.com/marius-m/wt4")
    }

    //endregion

    //region VersionPresenter

    override fun showProgress(progress: Float) {
        progressIndicator.progress = progress.toDouble()
    }

    override fun showUpdateInProgress() {
        buttonUpdate.text = Translation.getInstance().getString("upgrade_in_progress")
    }

    override fun showUpdateAvailable() {
        buttonUpdate.text = Translation.getInstance().getString("upgrade_available")
    }

    override fun showUpdateUnavailable() {
        buttonUpdate.text = Translation.getInstance().getString("upgrade_unavailable")
    }

    //endregion

    //region Listeners

    fun setDialogListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(VersionPresenter::class.java)!!
    }

}
