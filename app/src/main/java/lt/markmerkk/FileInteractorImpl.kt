package lt.markmerkk

import tornadofx.*
import java.io.File

class FileInteractorImpl: FileInteractor {
    override fun saveDirectory(): File? {
        return chooseDirectory()
    }
}