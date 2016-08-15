package lt.markmerkk.ui.version

import com.vinumeris.updatefx.UpdateSummary
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
import lt.markmerkk.interactors.VersionUpdater
import lt.markmerkk.interactors.VersioningInteractor
import lt.markmerkk.mvp.VersioningMvp
import lt.markmerkk.mvp.VersioningMvpPresenterImpl
import lt.markmerkk.ui.interfaces.DialogListener
import org.slf4j.LoggerFactory
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers

/**
 * Created by mariusmerkevicius on 12/14/15. Represents the presenter to update the log
 */
class VersionPresenter : Initializable, VersioningMvp.View {

    @Inject
    lateinit var config: Config
    @Inject
    lateinit var versionUpdaterInteractor: VersionUpdater<UpdateSummary>
    @Inject
    lateinit var versioningInteractor: VersioningInteractor<UpdateSummary>

    @FXML lateinit var buttonClose: Button
    @FXML lateinit var buttonTitle: Hyperlink
    @FXML lateinit var buttonAuthor: Hyperlink
    @FXML lateinit var buttonPlace: Hyperlink
    @FXML lateinit var buttonUpdate: Hyperlink
    @FXML lateinit var labelVersion: Text
    @FXML lateinit var progressIndicator: ProgressIndicator

    private var dialogListener: DialogListener? = null
    private lateinit var presenter: VersioningMvp.Presenter

    override fun initialize(location: URL, resources: ResourceBundle?) {
        Main.getComponent().presenterComponent().inject(this)
        presenter = VersioningMvpPresenterImpl(
                view = this,
                versionUpdaterInteractor = versionUpdaterInteractor,
                versioningInteractor = versioningInteractor,
                ioScheduler = Schedulers.computation(),
                uiScheduler = JavaFxScheduler.getInstance()
        )
        presenter.onAttach()
        labelVersion.text = String.format("Version: %s", config.versionName)
    }

    @PreDestroy
    fun destroy() {
        presenter.onDetach()
    }

    //region Action input

    fun onClickClose() {
        dialogListener?.onCancel()
        InjectorNoDI.forget(this)
    }

    fun onClickSave() {
    }

    fun onClickTitle() {
        if (Main.hostServices == null) return
        Main.hostServices.showDocument("https://bitbucket.org/mmerkevicius/wt4")
    }

    fun onClickAuthor() {
        if (Main.hostServices == null) return
        Main.hostServices.showDocument("https://github.com/marius-m")
    }

    fun onClickPlace() {
        if (Main.hostServices == null) return
        Main.hostServices.showDocument("http://ito.lt")
    }

    fun onClickUpdate() {
        //    if (versionController.getSummary() != null
        //        && versionController.getSummary().highestVersion > Main.VERSION_CODE) {
        //      versionController.upgrade();
        //      return;
        //    }
        //    versionController.checkForUpdate();
    }

    // todo : fix hyperlinks

    fun onHyperlinkGoogle1() {
        if (Main.hostServices == null) return
        Main.hostServices.showDocument("https://design.google.com/icons/")
    }

    fun onHyperlinkGoogle2() {
        if (Main.hostServices == null) return
        Main.hostServices.showDocument("https://creativecommons.org/licenses/by/4.0/")
    }

    fun onHyperlinkJFX1() {
        if (Main.hostServices == null) return
        Main.hostServices.showDocument("https://github.com/JFXtras/jfxtras")
    }

    fun onHyperlinkJFX2() {
        if (Main.hostServices == null) return
        Main.hostServices.showDocument("https://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22Revised_BSD_License.22.2C_.22New_BSD_License.22.2C_or_.22Modified_BSD_License.22.29")
    }

    fun onHyperlinkOthers() {
        if (Main.hostServices == null) return
        Main.hostServices.showDocument("https://bitbucket.org/mmerkevicius/wt4")
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
        val logger = LoggerFactory.getLogger(VersionPresenter::class.java)!!
    }

}
