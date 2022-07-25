package lt.markmerkk.widgets.help

import lt.markmerkk.repositories.ExternalResRepository
import lt.markmerkk.repositories.entities.ExternalRes
import org.slf4j.LoggerFactory

class HelpResourceLoader(
    private val externalResRepository: ExternalResRepository
) {

    private val helpFileMap: Map<ResourceHelp, ExternalRes>

    init {
        val externalResources = externalResRepository
            .externalResources(rootPath = ROOT_PATH)
            .toBlocking().value()
        helpFileMap = ResourceHelp.values().map { res ->
            val fileRes = externalResources.first { fileRes ->
                fileRes.name == res.resFileName
            }
            res to fileRes
        }.toMap()
    }

    fun helpResRaw(res: ResourceHelp): String {
        val imageRes = helpFileMap.getValue(res)
        return externalResRepository
            .readExternalResourceAsString(imageRes)
            .toBlocking().value()
    }

    companion object {
        private const val ROOT_PATH = "help"
        val l = LoggerFactory.getLogger(HelpResourceLoader::class.java)!!
    }
}

enum class ResourceHelp(val resFileName: String) {
    AR_ACTIVE("ar_help_active.md"),
    AR_ACTIVE_MULTI("ar_help_active_multi.md"),
    AR_INACTIVE("ar_help_inactive.md"),
    INVERT_PAGE("invert_page.md"),
    FIELD_TYPE("field_type.md"),
    PDF_READ("pdf-read.md"),
    PDF_READ_LANG("pdf-read-lang.md"),
    SHORTCUT_SAVE("shortcut-save.md"),
}