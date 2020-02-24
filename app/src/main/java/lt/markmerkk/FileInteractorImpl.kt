package lt.markmerkk

import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class FileInteractorImpl: FileInteractor {
    override fun saveDirectory(): File? {
        return chooseDirectory()
    }

    override fun selectFile(): File? {
        return chooseFile(
                title = "Select a file",
                filters = arrayOf(FileChooser.ExtensionFilter("Json file", "*.json")),
                mode = FileChooserMode.Single,
                owner = null
        ).firstOrNull()
    }
}