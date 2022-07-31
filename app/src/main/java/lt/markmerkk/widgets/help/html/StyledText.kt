package lt.markmerkk.widgets.help.html

class StyledText {
    private val mutableStyles: MutableList<StyleElement> = mutableListOf()

    fun elements(): List<StyleElement> {
        return mutableStyles
            .filter { it.text.isNotEmpty() }
            .toList()
    }

    fun text(): String {
        return elements()
            .map { it.text }
            .joinToString(separator = "")
    }

    fun appendTextBasic(text: String) {
        this.mutableStyles.add(ElementNoStyle(text = text))
    }

    fun appendTextWithStyles(text: String, styles: Map<String, String>) {
        val indexStart: Int = text().length
        val indexEnd: Int = indexStart + text.length
        this.mutableStyles.add(
            ElementStyleBasic(
                text = text,
                styles = styles,
                range = IntRange(
                    start = indexStart,
                    endInclusive = indexEnd,
                )
            )
        )
    }

    fun clear() {
        this.mutableStyles.clear()
    }

    sealed class StyleElement(
        val text: String,
    )
    class ElementNoStyle(
        text: String,
    ) : StyleElement(text = text)
    class ElementStyleBasic(
        text: String,
        val styles: Map<String, String>,
        val range: IntRange,
    ) : StyleElement(text = text) {
        fun stylesAsString(): String {
            return styles
                .map { entry -> "%s: %s;".format(entry.key, entry.value) }
                .joinToString(separator = " ")
        }
    }
}