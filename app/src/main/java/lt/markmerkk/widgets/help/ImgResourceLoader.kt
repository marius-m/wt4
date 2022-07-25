package lt.markmerkk.widgets.help

import lt.markmerkk.repositories.ExternalResRepository
import lt.markmerkk.repositories.entities.ExternalRes
import org.slf4j.LoggerFactory

class ImgResourceLoader(
    private val externalResRepository: ExternalResRepository
) {

    private val imageFileMap: Map<ResourceSvg, ExternalRes>

    init {
        val externalResources = externalResRepository
            .externalResources(rootPath = ROOT_PATH)
            .toBlocking()
            .value()
        l.debug("Found ${externalResources.size} entries. [$externalResources]")
        imageFileMap = ResourceSvg.values().map { res ->
            l.debug("Matching '$res' to $externalResources")
            val fileRes = externalResources.first { imageFile ->
                imageFile.name == res.resFileName
            }
            res to fileRes
        }.toMap()
    }

    fun imageResRaw(res: ResourceSvg): String {
        val imageRes = imageFileMap.getValue(res)
        return externalResRepository
            .readExternalResourceAsString(imageRes)
            .toBlocking().value()
    }

    companion object {
        private const val ROOT_PATH: String = "svgs"
        val l = LoggerFactory.getLogger(ImgResourceLoader::class.java)!!
    }
}

enum class ResourceSvg(val resFileName: String) {
    HELP("help_black_24dp.svg")
}